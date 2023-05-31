import {CloudTasksClient, protos} from "@google-cloud/tasks";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions";
import {TimeoutRequest} from "../model/request/TimeoutRequest";
import {throwFunctionsHttpsError} from "./functions_helper";


const tasksQueueName = defineString("TASKS_QUEUE_NAME");
const tasksLocation = defineString("LOCATION_TASKS");
const gServiceAccountEmail = defineString("G_SERVICE_ACCOUNT_EMAIL");

export default class CloudTasksUtil {
    private static _client = new CloudTasksClient();
    private readonly _tasksBaseUrl: string;
    private readonly _parent: string;

    constructor(
        private readonly _tasksQueueName: string = tasksQueueName.value(),
        private readonly _tasksLocation: string = tasksLocation.value(),
        private readonly _gServiceAccountEmail: string = gServiceAccountEmail.value(),
        private readonly _projectID: string = projectID.value(),
    ) {
        this._tasksBaseUrl = `https://${_tasksLocation}-${_projectID}.cloudfunctions.net`;
        this._parent = CloudTasksUtil._client.queuePath(this._projectID, this._tasksLocation, this._tasksQueueName);
    }

    public startTimer(
        request: TimeoutRequest,
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        return this.createHttpTaskWithSchedule(request, request.invokeFunctionName, Math.round(request.startStatusAt / 1000));
    }

    public cancelTimer(
        timerTaskName: string,
    ): Promise<boolean> {
        return CloudTasksUtil._client.deleteTask({name: timerTaskName})
            .catch((err) => {
                logger.warn(`Deletion task(${timerTaskName}) failed. maybe already consumed`, err);
            })
            .then(() => true);
    }

    public addExtraTime(
        timerTaskName: string,
        previousScheduleTime: number,
        extraTimeInSeconds: number,
    ) {
        return CloudTasksUtil._client.getTask({name: timerTaskName}).then(async ([task]) => {
            await this.cancelTimer(timerTaskName);

            return CloudTasksUtil._client.createTask({
                parent: this._parent,
                task: <protos.google.cloud.tasks.v2.ITask>{
                    httpRequest: task.httpRequest,
                    scheduleTime: Math.round(previousScheduleTime / 1000) + extraTimeInSeconds,
                },
            }, {maxRetries: 1});
        }).then(([response]) => {
            logger.info(`Changed task schedule time: ${response.name}, ${response.scheduleTime?.seconds}`);
            return response;
        });
    }

    private createHttpTaskWithSchedule(
        payload: TimeoutRequest,
        path: string,
        scheduleTimeInSeconds: number, // The schedule time in seconds
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        // Construct the fully qualified queue name.

        const task = this.createTaskObject(
            `${this._tasksBaseUrl}/${path}`,
            payload,
            scheduleTimeInSeconds
        );

        // Send create task request.
        return CloudTasksUtil._client.createTask({parent: this._parent, task}, {maxRetries: 1})
            .then(([response]) => {
                logger.info(`Created task ${response.name}`);
                return response;
            });
    }

    private createTaskObject(
        url: string,
        payload: object,
        scheduleTimeInSeconds: number,
    ): protos.google.cloud.tasks.v2.ITask {
        const body = Buffer.from(JSON.stringify(payload)).toString("base64");
        return <protos.google.cloud.tasks.v2.ITask>{
            httpRequest: {
                httpMethod: "POST",
                url,
                oidcToken: {
                    serviceAccountEmail: this._gServiceAccountEmail,
                    audience: new URL(url).origin,
                },
                body,
                headers: {
                    "Content-Type": "application/json",
                },
            },
            scheduleTime: {
                seconds: scheduleTimeInSeconds,
            },
        };
    }
}

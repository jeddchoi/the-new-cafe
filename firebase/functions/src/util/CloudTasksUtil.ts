import {CloudTasksClient, protos} from "@google-cloud/tasks";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions";
import {UserSeatUpdateRequest} from "../model/UserSeatUpdateRequest";


const tasksQueueName = defineString("TASKS_QUEUE_NAME");
const tasksLocation = defineString("LOCATION_TASKS");
const gServiceAccountEmail = defineString("G_SERVICE_ACCOUNT_EMAIL");

export class CloudTasksUtil {
    private static _client = new CloudTasksClient();
    private readonly _tasksBaseUrl: string;

    constructor(
        private readonly _tasksQueueName: string = tasksQueueName.value(),
        private readonly _tasksLocation: string = tasksLocation.value(),
        private readonly _gServiceAccountEmail: string = gServiceAccountEmail.value(),
        private readonly _projectID: string = projectID.value(),
    ) {
        this._tasksBaseUrl = `https://${_tasksLocation}-${_projectID}.cloudfunctions.net`;
    }

    public reserveUserSeatUpdate(
        request: UserSeatUpdateRequest,
        invokeFnPath: string,
        scheduleTimeInSeconds: number,
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        return this.createHttpTaskWithSchedule(request, invokeFnPath, scheduleTimeInSeconds);
    }

    public createHttpTaskWithSchedule(
        payload = {}, // The task HTTP request body
        path: string,
        scheduleTimeInSeconds: number, // The schedule time in seconds
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        // Construct the fully qualified queue name.
        const parent = CloudTasksUtil._client.queuePath(this._projectID, this._tasksLocation, this._tasksQueueName);
        const taskPath = CloudTasksUtil._client.taskPath(this._projectID, this._tasksLocation, this._tasksQueueName, "TASK");
        logger.log(`taskPath = ${taskPath}`);
        const task = this.createTaskObject(
            `${this._tasksBaseUrl}${path}`,
            this._gServiceAccountEmail,
            payload,
            scheduleTimeInSeconds
        );

        // Send create task request.
        return CloudTasksUtil._client.createTask({parent, task}, {maxRetries: 1})
            .then(([response]) => {
                logger.info(`Created task ${response.name}`);
                return response;
            });
    }

    private createTaskObject(
        url: string,
        serviceAccountEmail: string,
        payload: object,
        scheduleTimeInSeconds: number,
    ): protos.google.cloud.tasks.v2.ITask {
        const body = Buffer.from(JSON.stringify(payload)).toString("base64");
        return <protos.google.cloud.tasks.v2.ITask>{
            httpRequest: {
                httpMethod: "POST",
                url,
                oidcToken: {
                    serviceAccountEmail,
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

export default CloudTasksUtil;

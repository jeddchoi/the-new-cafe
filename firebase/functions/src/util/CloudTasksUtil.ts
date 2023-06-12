import {CloudTasksClient, protos} from "@google-cloud/tasks";
import {defineString, projectID} from "firebase-functions/params";
import {logger} from "firebase-functions";

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
        // noinspection SpellCheckingInspection
        this._tasksBaseUrl = `https://${_tasksLocation}-${_projectID}.cloudfunctions.net`;
        this._parent = CloudTasksUtil._client.queuePath(this._projectID, this._tasksLocation, this._tasksQueueName);
    }

    public startRemoveTimer(
        deletePath: string,
        scheduleDate: number,
        taskName: string,
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        return this.createHttpTaskWithSchedule({deletePath}, "onDeletePathTimeout", Math.round(scheduleDate / 1000), this.getFullTaskName(taskName));
    }


    public cancelTimer(
        taskName: string,
    ): Promise<void> {
        return CloudTasksUtil._client.deleteTask({name: this.getFullTaskName(taskName)})
            .catch((err) => {
                logger.warn(`Deletion task(${taskName}) failed. maybe already consumed`, err);
            }).then();
    }

    private getFullTaskName(taskId: string) {
        const fullName = CloudTasksUtil._client.taskPath(this._projectID, this._tasksLocation, this._tasksQueueName, taskId);
        logger.log(`fullName = ${fullName}`);
        return fullName;
    }

    private createHttpTaskWithSchedule(
        payload: NonNullable<any>,
        path: string,
        scheduleTimeInSeconds: number, // The schedule time in seconds
        taskName: string | null,
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        // Construct the fully qualified queue name.

        const task = this.createTaskObject(
            `${this._tasksBaseUrl}/${path}`,
            payload,
            scheduleTimeInSeconds,
            taskName
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
        taskName: string | null,
    ): protos.google.cloud.tasks.v2.ITask {
        const body = Buffer.from(JSON.stringify(payload)).toString("base64");
        return <protos.google.cloud.tasks.v2.ITask>{
            name: taskName,
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

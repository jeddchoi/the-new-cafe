import {CloudTasksClient, protos} from "@google-cloud/tasks";
import {logger} from "firebase-functions";
import {defineString, projectID} from "firebase-functions/params";

export class CloudTasksUtil {
    private static _client = new CloudTasksClient();
    private readonly _tasksBaseUrl: string;

    constructor(
        private readonly _tasksQueueName: string,
        private readonly _tasksLocation: string,
        private readonly _gServiceAccountEmail: string,
        private readonly _projectID: string
    ) {
        this._tasksBaseUrl = `https://${_tasksLocation}-${_projectID}.cloudfunctions.net`;
    }
    private createHttpTaskWithSchedule(
        path: string,
        payload = {}, // The task HTTP request body
        scheduleTimeInSeconds: number, // The schedule time in seconds
    ): Promise<protos.google.cloud.tasks.v2.ITask> {
        // Construct the fully qualified queue name.
        const parent = CloudTasksUtil._client.queuePath(this._projectID, this._tasksLocation, this._tasksQueueName);
        const task = this.createTaskObject(
            `${this._tasksBaseUrl}${path}`,
            this._gServiceAccountEmail,
            payload,
            scheduleTimeInSeconds
        );

        // Send create task request.
        return CloudTasksUtil._client.createTask({parent, task})
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

const cloudTasksUtil = new CloudTasksUtil(
    defineString("TASKS_QUEUE_NAME").value(),
    defineString("MY_TASKS_LOCATION").value(),
    defineString("G_SERVICE_ACCOUNT_EMAIL").value(),
    projectID.value(),
);
export default cloudTasksUtil;

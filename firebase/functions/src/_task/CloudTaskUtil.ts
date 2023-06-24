import {CloudTasksClient, protos} from "@google-cloud/tasks";
import {logger} from "firebase-functions/v2";


export default class CloudTaskUtil {
    private static cloudTasksClient: CloudTasksClient = new CloudTasksClient();
    private static _instance: CloudTaskUtil;
    private queuePath: string;

    constructor(
        private projectId: string,
        private location: string,
        private queueName: string,
    ) {
        logger.debug("[CloudTaskUtil] constructor");
        this.queuePath = CloudTaskUtil.cloudTasksClient.queuePath(projectId, location, queueName);
    }

    public static getInstance(
        projectId: string,
        location: string,
        queueName: string
    ) {
        logger.debug("[CloudTaskUtil] getInstance");
        return this._instance || (this._instance = new this(projectId, location, queueName));
    }

    createOneShotHttpPostTask(
        taskId: string,
        url: string,
        scheduleTimeInSeconds: number | null,
        payload: object,
    ) {
        return CloudTaskUtil.cloudTasksClient.createTask(
            {
                parent: this.queuePath,
                task: <protos.google.cloud.tasks.v2.ITask>{
                    name: this.getTaskName(taskId),
                    httpRequest: {
                        httpMethod: "POST",
                        url,
                        body: Buffer.from(JSON.stringify(payload)).toString("base64"),
                        headers: {
                            "Content-Type": "application/json",
                        },
                    },
                    scheduleTime: {
                        seconds: scheduleTimeInSeconds,
                    },
                },
            },
            {
                retry: null,
            }
        ).then(([response]) => {
            logger.debug("[CloutTaskUtil] Created task", {response});
            return response;
        });
    }

    cancelTask(
        taskId: string,
    ) {
        return CloudTaskUtil.cloudTasksClient.deleteTask({
            name: this.getTaskName(taskId),
        });
    }

    getTaskName(taskId: string) {
        return CloudTaskUtil.cloudTasksClient.taskPath(this.projectId, this.location, this.queueName, taskId);
    }
}

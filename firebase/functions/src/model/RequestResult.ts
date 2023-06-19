import {BusinessResultCode} from "./BusinessResultCode";

export interface RequestResult {
    code: BusinessResultCode;
    message: string;
}

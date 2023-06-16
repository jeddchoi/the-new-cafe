import {BusinessResultCode} from "../model/BusinessResultCode";

export function isBusinessResultCode(error: any): error is BusinessResultCode {
    return Object.values(BusinessResultCode).includes(error);
}

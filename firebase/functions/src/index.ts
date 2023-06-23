import "./helper/firebase-initialize";
import "reflect-metadata";


exports.SeatFinder = require("./seat-finder");

if (process.env.FUNCTIONS_EMULATOR === "true") {
    exports.Database = require("./_database");
    exports.Firestore = require("./_firestore");
    exports.Task = require("./_task");
}


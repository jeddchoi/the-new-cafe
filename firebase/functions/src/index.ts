import "./helper/firebase-initialize";

// exports.seatFinder = require("./seat-finder");

if (process.env.FUNCTIONS_EMULATOR === "true") {
    exports.Database = require("./_database");
    exports.Firestore = require("./_firestore");
} else {
    // exports.Task = require("./_task");
}

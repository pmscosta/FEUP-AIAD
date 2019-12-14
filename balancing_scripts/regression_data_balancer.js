const fs = require("fs");

const NUM_CLASSES = 6;
const CLASS_SIZE = 667;
const output = [];

const readFile = (file_name) => (
    fs.readFileSync(file_name).toString().split("\n")
);

if (process.argv.length < 4) {
    console.error("Not enough arguments.\n");
    console.error("usage: node regression_parser.js <input_file> <partition_size>\n");
    process.exit(1);
}

const theClass = (num) => `D${num}`;

const getClass = (trades) => {
    for (let i = 1; i <= NUM_CLASSES; ++i) {
        if (trades <= (CLASS_SIZE * i)) {
            return theClass(i);
        }
    }

    return theClass(NUM_CLASSES + 1);
}

const counts = {};
for (let i = 1; i <= NUM_CLASSES + 1; ++i) {
    counts[theClass(i)] = 0;
}

const file_name = process.argv[2];
const target_class_count = parseInt(process.argv[3], 10);
const data = readFile(file_name);

data.forEach((entry) => {
    const trades = entry.split(",").pop();

    const trades_class = getClass(trades);

    if (trades_class !== theClass(NUM_CLASSES + 1) && counts[trades_class] < target_class_count) {
        counts[trades_class]++;
        output.push(entry.split(",").join(","));
    }
});

console.log(output.join("\n"));

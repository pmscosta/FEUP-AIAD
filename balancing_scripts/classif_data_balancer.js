const fs = require("fs");

const readFile = (file_name) => (
    fs.readFileSync(file_name).toString().split("\n")
);

if (process.argv.length < 3) {
    console.error("Not enough arguments.\n");
    console.error("usage: node classif_parser.js <input_file>\n");
    process.exit(1);
}

const getClass = (percentage) => {
    if (percentage < 25) {
        return "0-24";
    } else if (percentage < 50) {
        return "25-49";
    } else if (percentage < 75) {
        return "50-74";
    } else if (percentage <= 100) {
        return "75-100";
    } else {
        throw new Error(`Bad Percetage: ${percentage}`);
    }
};

const counts = {
    "0-24": 0,
    "25-49": 0,
    "50-74": 0,
    "75-100": 0,
};

const outputed_counts = { ...counts };
const dups = {};
const no_dups_data = [];
const output = [];

const file_name = process.argv[2];
const data = readFile(file_name);

data.forEach((entry) => {
    const percentage = entry.split(",").pop();

    if (dups[entry]) {
        num_dups_found++;
        return;
    }
    no_dups_data.push(entry);
    dups[entry] = true;

    const percentage_class = getClass(percentage);
    counts[percentage_class]++;
});

const min = Math.min(...Object.values(counts));

no_dups_data.forEach((entry) => {
    const percentage = entry.split(",").pop();

    const percentage_class = getClass(percentage);

    if (outputed_counts[percentage_class] < min) {
        outputed_counts[percentage_class]++;
        output.push(entry);
    }
});

console.log(output.join("\n"));

const fs = require("fs");

const OUTLIER_BOUND = 4000;

const readFile = (file_name) => (
    fs.readFileSync(file_name).toString().split("\n")
);

if (process.argv.length < 3) {
    console.error("Not enough arguments.\n");
    console.error("usage: node classif_parser.js <input_file> <is_classification (optional)>\n");
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

const dups = {};

const file_name = process.argv[2];
const is_classification = process.argv[3];
const data = readFile(file_name);

const statistics = {
    num_entries: data.length,
    num_duplicate_entries: 0,
    num_outliers: 0,
}

if (is_classification) {
    statistics.classes = counts;
}

data.forEach((entry) => {
    const percentage = entry.split(",").pop();

    if (!is_classification && percentage > OUTLIER_BOUND) {
        statistics.num_outliers++;
    }

    if (dups[entry]) {
        statistics.num_duplicate_entries++;
        return;
    }
    dups[entry] = true;

    if (is_classification) {
        const percentage_class = getClass(percentage);
        counts[percentage_class]++;
    }
});

console.log(JSON.stringify(statistics, null, 2));

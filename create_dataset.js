const execSync = require('child_process').execSync;

const run = (args) => {
    execSync(`java -cp compiled:lib/jade.jar Main ${args.join(" ")}`);
}

const invalidProduction = ([clay_prod, food_prod, stone_prod, wood_prod], consumption_rate) => {
    return (Math.min(clay_prod, food_prod, stone_prod, wood_prod) >= consumption_rate) ||
        (Math.max(clay_prod, food_prod, stone_prod, wood_prod) <= consumption_rate);
}

const VILLAGE_NR_MIN = 1;
const VILLAGE_NR_MAX = 3;
const VILLAGE_NR_STEP = 1;

const INIT_RESOURCE_MIN = 200;
const INIT_RESOURCE_MAX = 200;
const INIT_RESOURCE_STEP = 100;

const CONSUMPTION_RATE_MIN = 200;
const CONSUMPTION_RATE_MAX = 200;
const CONSUMPTION_RATE_STEP = 100;

const PROD_RATE_MIN = 150;
const PROD_RATE_MAX = 250;
const PROD_RATE_STEP = 50;

let n_runs = 0;

for (let passive_nr = VILLAGE_NR_MIN; passive_nr <= VILLAGE_NR_MAX; passive_nr += VILLAGE_NR_STEP)
for (let greedy_nr = VILLAGE_NR_MIN; greedy_nr <= VILLAGE_NR_MAX; greedy_nr += VILLAGE_NR_STEP)
for (let smart_nr = VILLAGE_NR_MIN; smart_nr <= VILLAGE_NR_MAX; smart_nr += VILLAGE_NR_STEP)
for (let init_resource_passive = INIT_RESOURCE_MIN; init_resource_passive <= INIT_RESOURCE_MAX; init_resource_passive += INIT_RESOURCE_STEP)
for (let init_resource_greedy = INIT_RESOURCE_MIN; init_resource_greedy <= INIT_RESOURCE_MAX; init_resource_greedy += INIT_RESOURCE_STEP)
for (let init_resource_smart = INIT_RESOURCE_MIN; init_resource_smart <= INIT_RESOURCE_MAX; init_resource_smart += INIT_RESOURCE_STEP)
for (let production_rate_passive = PROD_RATE_MIN; production_rate_passive <= PROD_RATE_MAX; production_rate_passive += PROD_RATE_STEP)
for (let production_rate_greedy = PROD_RATE_MIN; production_rate_greedy <= PROD_RATE_MAX; production_rate_greedy += PROD_RATE_STEP)
for (let production_rate_smart = PROD_RATE_MIN; production_rate_smart <= PROD_RATE_MAX; production_rate_smart += PROD_RATE_STEP)
for (let passive_null_prod_resource_idx = 0; passive_null_prod_resource_idx < 4; ++passive_null_prod_resource_idx)
for (let greedy_null_prod_resource_idx = 0; greedy_null_prod_resource_idx < 4; ++greedy_null_prod_resource_idx)
for (let smart_null_prod_resource_idx = 0; smart_null_prod_resource_idx < 4; ++smart_null_prod_resource_idx)
for (let consumption_rate_passive = CONSUMPTION_RATE_MIN; consumption_rate_passive <= CONSUMPTION_RATE_MAX; consumption_rate_passive += CONSUMPTION_RATE_STEP)
for (let consumption_rate_greedy = CONSUMPTION_RATE_MIN; consumption_rate_greedy <= CONSUMPTION_RATE_MAX; consumption_rate_greedy += CONSUMPTION_RATE_STEP)
for (let consumption_rate_smart = CONSUMPTION_RATE_MIN; consumption_rate_smart <= CONSUMPTION_RATE_MAX; consumption_rate_smart += CONSUMPTION_RATE_STEP) {

    const production_passive = new Array(4).fill(production_rate_passive);
    production_passive[passive_null_prod_resource_idx] = 0;
    const production_greedy = new Array(4).fill(production_rate_greedy);
    production_greedy[greedy_null_prod_resource_idx] = 0;
    const production_smart = new Array(4).fill(production_rate_smart);
    production_smart[smart_null_prod_resource_idx] = 0;

    if (
        (passive_nr + greedy_nr + smart_nr) < 1 ||
        invalidProduction(production_passive, consumption_rate_passive) ||
        invalidProduction(production_greedy, consumption_rate_greedy) ||
        invalidProduction(production_smart, consumption_rate_smart)
    ) {
        continue;
    }

    n_runs++;

    const params = [
        passive_nr, init_resource_passive, consumption_rate_passive, ...production_passive,
        greedy_nr, init_resource_greedy, consumption_rate_greedy, ...production_greedy,
        smart_nr, init_resource_smart, consumption_rate_smart, ...production_smart,
    ];

    console.log(`Running #${n_runs}`);
    run(params);
    console.log(`Ran #${n_runs}`);
}

console.log(`Did ${n_runs} runs`);

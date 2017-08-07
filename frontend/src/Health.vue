<style lang="scss" scoped>
.health-container {
    width: 100%;
    height: 100%;
    padding-bottom: 20px;

    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
}

.tree-display {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;

    overflow: hidden;
}
</style>

<template>
    <div class="container-fluid health-container">
        <div class="page-header">
            <h1>Health Checks</h1>
        </div>
        <tree-display class="tree-display" :treeData="healthChecks"></tree-display>
    </div>
</template>

<script>
import TreeDisplay from './TreeDisplay.vue';
import HealthStatus from './healthstatus.js';

let initialized = false;

export default {
    name: 'Health Checks',
    components: {
        'tree-display': TreeDisplay
    },
    data() {
        return {
            healthChecks: {},
        }
    },
    beforeMount() {
        if (!initialized) {
            HealthStatus.initialize('/healthchecks');
            initialized = true;
        }
        this.healthCallback = healthJson => this.healthChecks = healthJson;
        HealthStatus.addCallback(this.healthCallback);
    },
    beforeDestroy() {
        HealthStatus.removeCallback(this.healthCallback);
    }
}
</script>
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
        /* Chrome will log errors no matter what: 
           https://stackoverflow.com/questions/21990036/prevent-google-chrome-log-xmlhttprequest
        */
        const updateStatuses = () => this.$http.get(window.location.pathname + '/healthchecks',
            { validateStatus: status => (status >= 200 && status < 300) || status === 500 || status === 503 })
            .then(response => response.data)
            .then(healthJson => {
                healthJson.id = 'root';
                healthJson.status = healthJson.outcome;

                function setAddress(parent, node) {
                    node.address = parent ? parent.address + '/' + node.id : node.id;
                    if (node.checks) {
                        for (let checkObj of node.checks) {
                            setAddress(node, checkObj);
                        }
                    }
                }

                setAddress(null, healthJson);
                this.healthChecks = healthJson;
            });
        updateStatuses();
        this.updateTask = setInterval(updateStatuses, 1000);
    },
    beforeDestroy() {
        clearInterval(this.updateTask);
    }
}
</script>
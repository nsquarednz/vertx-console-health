<style lang="scss" scoped>

</style>

<template>
    <div class="container-fluid status-container">
    </div>
</template>

<script>
export default {
    name: 'Health',
    data() {
        return {
            healthChecks: {},
        }
    },
    beforeMount() {
        const updateStatuses = () => this.$http.get(window.location.pathname + '/healthchecks')
            .then(response => response.json(), errResponse => {
                if (errResponse.status === 500 || errResponse.status === 503) {
                    return JSON.parse(errResponse.bodyText)
                } else {
                    throw 'Failed to retrieve health checks: ' + errResponse.statusText
                }
            })
            .then(
                healthJson => console.log(healthJson)
            );
        updateStatuses();
        this.updateTask = setInterval(updateStatuses, 1000);
    },
    beforeDestroy() {
        clearInterval(this.updateTask);
    }
}
</script>
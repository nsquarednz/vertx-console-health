<template>
    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card-pf card-pf-view card-pf-view-select card-pf-view-single-select check-card" :class="checkCardClass">
            <div class="card-pf-body">
                <div class="check-card-header">
                    <div class="check-id">{{ statusCheck.id }}</div>
                    <div class="check-status">{{ statusCheck.status }}</div>
                </div>
                {{ statusCheck.uptimeRatio }}
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        statusCheck: Object
    },
    computed: {
        checkCardClass() {
            if (this.statusCheck.stateLevel === 0) {
                return 'status-card-down';
            } else if (this.statusCheck.stateLevel === 1) {
                return 'status-card-up';
            } else {
                return null;
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.check-card-header {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
}

.check-status {
    margin-left: auto;
}
</style>
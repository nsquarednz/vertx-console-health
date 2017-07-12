<template>
    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card-pf card-pf-view card-pf-view-select card-pf-view-single-select check-card" :class="checkCardClass">
            <div class="card-pf-body">
                <div class="check-card-header">
                    <div class="check-id">{{ statusCheck.id }}</div>
                    <div class="check-status">{{ statusCheck.status }}</div>
                </div>
                <pf-donut-util :data="circleData" centerLabelType="percent"></pf-donut-util>
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
                return 'check-card-down';
            } else if (this.statusCheck.stateLevel === 1) {
                return 'check-card-up';
            } else {
                return null;
            }
        },
        circleData() {
            return {
                total: 100,
                used: Math.round(this.statusCheck.uptimeRatio * 100),
                formatFn: () => 'uptime'
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.check-card {
    padding: 0;
    transition: background-color 2s;
    color: #fff;
    border: none;
    
    & /deep/ .c3-chart-arcs-title {
        fill: #fff;
    }

    &.check-card-up {
        background-color: #3f9c35;
    }

    &.check-card-down {
        background-color: #c00;
    }
}

.check-card-header {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    padding: 10px;
}

.check-id {
    font-size: 15px;
    line-height: 15px;
}

.check-status {
    margin-left: auto;
    font-weight: bold;
    line-height: 15px;
}
</style>
<template>
    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-2">
        <div class="card-pf card-pf-view card-pf-view-select card-pf-view-single-select check-card" :class="checkCardClass">
            <div class="card-pf-body">
                <div class="status-view">
                    <div class="check-card-header">
                        <div class="check-id">{{ statusCheck.id }}</div>
                        <div class="check-status">{{ statusCheck.status }}</div>
                    </div>
                    <pf-donut-util class="uptime-donut" :data="circleData" centerLabelType="percent" :extraChartOptions="arcOptions"></pf-donut-util>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        statusCheck: Object
    },
    beforeMount() {
        this.arcOptions = {
            color: {
                pattern: ['#fff', 'rgba(255,255,255,0.33)']
            },
            tooltip: {
                show: false
            }
        };
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

    & /deep/ .c3-chart-arc path {
        stroke: none;
    }

    &.check-card-up {
        background-color: #3f9c35;
    }

    &.check-card-down {
        background-color: #c00;
    }

    .card-pf-body {
        background-image: linear-gradient(transparent, rgba(255, 255, 255, 0.15));
        padding: 10px;
    }
}

$card-height: 220px;

.status-view {
    height: $card-height;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
}

.check-card-header {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
}

.check-id {
    font-size: 15px;
    line-height: 15px;
}

.check-status {
    margin-left: auto;
    font-weight: bold;
    line-height: 15px;
    padding-left: 10px;
}

.uptime-donut {
    pointer-events: none;
    margin-top: auto;
    margin-bottom: auto;
}
</style>
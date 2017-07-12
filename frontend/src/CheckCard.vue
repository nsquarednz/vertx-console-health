<template>
    <div class="col-xs-12 col-sm-6 col-md-4 col-lg-3">
        <div class="card-pf card-pf-view card-pf-view-select card-pf-view-single-select check-card" :class="checkCardClass">
            <div class="card-pf-body">
                <div class="status-view" @mouseover="displayOverlay = true" :class="{ 'below-overlay': displayOverlay}">
                    <div class="check-card-header">
                        <div class="check-id">{{ statusCheck.id }}</div>
                        <div class="check-status">{{ statusCheck.status }}</div>
                    </div>
                    <pf-donut-util class="uptime-donut" :data="circleData" centerLabelType="percent" :extraChartOptions="arcOptions"></pf-donut-util>
                </div>
                <div class="toggle-overlay" :class="{ shown: displayOverlay }" @mouseleave="displayOverlay = false">
                    <div class="check-card-header">
                        <div class="check-id">{{ statusCheck.id }}</div>
                        <div class="check-status">{{ statusCheck.status }}</div>
                    </div>
                    <div class="check-card-data">
                        <code class="json-data" v-html="formattedDataString"></code>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import jsonMarkup from 'json-markup';

export default {
    props: {
        statusCheck: Object
    },
    data() {
        return {
            displayOverlay: false
        }
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
        },
        formattedDataString() {
            return jsonMarkup(this.statusCheck.data || {});
        }
    }
}
</script>

<style lang="scss" scoped>
$card-padding: 10px;

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
        padding: $card-padding;
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
    transition: all 0.2s;
}

.below-overlay {
    opacity: 0.4;
    filter: blur(3px);
}

.toggle-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    pointer-events: none;
    opacity: 0;
    transition: all 0.2s;
    top: 0;
    left: 0;
    padding: $card-padding;
    &.shown {
        opacity: 1;
        pointer-events: auto;
    }
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

.check-card-data {
    margin-top: $card-padding;

    .json-data {
        white-space: pre;
        background: none;
        padding: 0;
        color: #fff;
    }
}
</style>
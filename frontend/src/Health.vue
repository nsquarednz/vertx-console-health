<style lang="scss" scoped>
.status-container {
    overflow-x: hidden;

    &>.row {
        padding: 0 10px;
        &:first-child {
            padding-top: 20px;
        }
    }
}

.status-list-enter-active,
.status-list-leave-active {
    transition: all 0.5s;
    &>.card {
        opacity: 0;
        transform: scale(0.25, 0.25);
    }
}

.status-list-leave-active {
    position: absolute;
    z-index: -1;
    &>.card {
        transition: all 0.2s;
    }
}

.status-list-move {
    transition: transform 0.2s ease-in-out;
}

.status-filter,
.status-sort {
    margin: 0 -10px 20px;
}

.status-sort {
    float: right;

    .btn-group.bootstrap-select {
        width: 120px;
    }
}
</style>

<template>
    <div class="container-fluid status-container">
        <div class="row status-toolbar">
            <div class="col-xs-12 col-sm-6 col-md-4 col-lg-3">
                <div class="status-filter">
                    <label for="filter" class="sr-only">Filter</label>
                    <input type="text" id="filter" class="form-control" placeholder="Filter" v-model="filterQuery">
                </div>
            </div>
            <div class="col-xs-12 col-sm-offset-3 col-sm-3 col-md-offset-6 col-md-2 col-lg-offset-7 col-lg-2">
                <div class="status-sort">
                    <v-select v-model="sortType" :options="sortOptions"></v-select>
                </div>
            </div>
        </div>
        <transition-group :name="filtering ? null : 'status-list'" tag="div" class="row">
            <check-card v-for="status in sortedStatuses" :key="status.id" v-show="isDisplayed(status.id)" :statusCheck="status"></check-card>
        </transition-group>
    </div>
</template>

<script>
import CheckCard from './CheckCard.vue';

const STATUS_SORT = 'Sort by Status';
const NAME_SORT = 'Sort by Name';
const UPTIME_SORT = 'Sort by Uptime';

export default {
    name: 'Health',
    components: {
        'check-card': CheckCard
    },
    data() {
        return {
            statuses: {},
            filterQuery: '',
            filtering: false,
            sortType: STATUS_SORT
        }
    },
    methods: {
        isDisplayed(name) {
            if (this.processedFilterQuery === '') {
                return true;
            } else {
                return name.toLowerCase().includes(this.processedFilterQuery);
            }
        }
    },
    computed: {
        sortedStatuses() {
            const statusValues = Object.keys(this.statuses)
                .map(k => this.statuses[k]);
            const nameComparator = (a, b) => {
                const aName = a.id.toUpperCase();
                const bName = b.id.toUpperCase();
                if (aName < bName) {
                    return -1;
                } else if (aName > bName) {
                    return 1;
                } else {
                    return 0;
                }
            };
            if (this.sortType === STATUS_SORT) {
                statusValues.sort((a, b) => {
                    const aLevel = a.stateLevel;
                    const bLevel = b.stateLevel;
                    if (aLevel === bLevel) {
                        return nameComparator(a, b);
                    } else {
                        return aLevel - bLevel;
                    }
                });
            } else if (this.sortType === NAME_SORT) {
                statusValues.sort(nameComparator);
            } else if (this.sortType === UPTIME_SORT) {
                statusValues.sort((a, b) => b.uptimeRatio - a.uptimeRatio);
            }
            return statusValues;
        },
        processedFilterQuery() {
            return this.filterQuery.trim().toLowerCase();
        }
    },
    watch: {
        filterQuery() {
            if (!this.filtering) {
                // Disable animations when filtering
                this.filtering = true;
                this.$nextTick(() => this.filtering = false);
            }
        }
    },
    beforeMount() {
        const updateStatuses = () => this.$http.get(window.location.pathname + '/health/status')
            .then(response => response.json())
            .then(checks => {
                for (let el of checks) {
                    el.stateLevel = el.status === 'UP' ? 1 : 0;
                    this.$set(this.statuses, el.id, el);
                }
            });
        updateStatuses();
        this.updateTask = setInterval(updateStatuses, 1000);
        this.sortOptions = [STATUS_SORT, NAME_SORT, UPTIME_SORT];
    },
    beforeDestroy() {
        clearInterval(this.updateTask);
    }
}
</script>
<template>
    <div class="tree-container" v-resize:throttle="drawTree">
    </div>
</template>

<style lang="scss" scoped>
.tree-container /deep/ svg {
    width: 100%;
    height: 100%;
    stroke-width: 0px;
    background-color: blue;

}
</style>

<script>
import resize from 'vue-resize-directive';

export default {
    props: {
        treeData: Object
    },
    directives: {
        resize
    },
    mounted() {
        this.$nextTick(this.drawTree);
    },
    methods: {
        drawTree() {
            const container = this.$el;
            const sel = d3.select(container);
            const width = container.clientWidth, height = container.clientHeight;
            console.log(width + ',' + height)

            const tree = d3.layout.tree().size([height, width]);
            const diagonal = d3.svg.diagonal().projection(d => [d.y, d.x]);

            let treeSvg = sel.select('svg');
            if (treeSvg.empty()) {
                treeSvg = sel.attr('preserveAspectRatio', 'none').append('svg').append('g');
            } else {
                treeSvg.selectAll('*').remove();
            }


        }
    }
}
</script>

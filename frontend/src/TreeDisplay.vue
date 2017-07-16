<template>
    <div class="health-tree-container" v-resize:throttle="drawTree">
    </div>
</template>

<style lang="scss">
.health-tree-container /deep/ svg {
    width: 100%;
    height: 100%;

    .node text {
        font-family: Menlo, Monaco, Consolas, monospace;
    }

    .link {
        fill: none;
        stroke: #d1d1d1;
        stroke-width: 2px;
    }
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
    watch: {
        treeData(val) {
            this.drawTree();
        }
    },
    methods: {
        drawTree() {
            const container = this.$el;
            const sel = d3.select(container);
            const width = container.clientWidth, height = container.clientHeight;

            const tree = d3.layout.tree().size([height, width]);
            const diagonal = d3.svg.diagonal().projection(d => [d.y, d.x]);

            let treeSvg = sel.select('svg');
            if (treeSvg.empty()) {
                treeSvg = sel.attr('preserveAspectRatio', 'none').append('svg').append('g');
            } else {
                treeSvg.selectAll('*').remove();
            }

            const rootElement = Object.assign({}, JSON.parse(JSON.stringify(this.treeData)));
            d3.layout.hierarchy().children(d => d.checks)(rootElement);

            // Styling props
            const leftMargin = 50;
            const COLOR_RED = "#cc0000";
            const COLOR_GREEN = "#6ec664";

            let i = 0;
            // Compute the new tree layout.
            var nodes = tree.nodes(rootElement).reverse(),
                links = tree.links(nodes);

            // Normalize for fixed-depth.
            nodes.forEach(d => d.y = d.depth * 120);

            // Declare the nodes…
            var node = treeSvg.selectAll("g.node")
                .data(nodes, d => d.uid || (d.uid = ++i));

            // Enter the nodes.
            var nodeEnter = node.enter().append("g")
                .attr("class", "node")
                .attr("transform", d => "translate(" + (d.y + leftMargin) + "," + d.x + ")");

            nodeEnter.append("circle")
                .attr("r", 10)
                .style("fill", d => d.status === 'UP' ? COLOR_GREEN : COLOR_RED);

            nodeEnter.append("text")
                .attr("x", d => d.children ? -16 : 16)
                .attr("dy", ".35em")
                .attr("text-anchor", d => d.children ? "end" : "start")
                .text(d => d.id)
                .style("fill-opacity", 1);

            // Declare the links…
            var link = treeSvg.selectAll("path.link")
                .data(links, d => d.target.uid);

            // Enter the links.
            link.enter().insert("path", "g")
                .attr("class", "link")
                .attr("transform", d => "translate(" + leftMargin + ")")
                .attr("d", diagonal);
        }
    }
}
</script>

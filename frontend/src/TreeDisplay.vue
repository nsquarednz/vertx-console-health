<template>
    <div class="health-tree-container" v-resize:throttle="drawTree">
    </div>
</template>

<style lang="scss">
.health-tree-container svg {
    width: 100%;
    height: 100%;

    .node text {
        font-family: Menlo, Monaco, Consolas, monospace;
    }

    .node circle {
        stroke-width: 1px;
        filter:url(#shadow);
    }

    .link {
        fill: none;
        stroke: #d8d8d8;
        stroke-width: 2px;
    }
}
</style>

<script>
import resize from 'vue-resize-directive';

function setGradientStops(gradient, start, end) {
    gradient
        .attr("x1", "0%")
        .attr("x2", "0%")
        .attr("y1", "0%")
        .attr("y2", "100%")

    gradient.append("stop")
        .attr('class', 'start')
        .attr("offset", "0%")
        .attr("stop-color", start)
        .attr("stop-opacity", 1);

    gradient.append("stop")
        .attr('class', 'end')
        .attr("offset", "100%")
        .attr("stop-color", end)
        .attr("stop-opacity", 1);
}

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

            const defs = treeSvg.append("defs");
            const redGradient = defs.append("linearGradient").attr("id", "redGradient");
            setGradientStops(redGradient, "#c00", "#a30000");
            const greenGradient = defs.append("linearGradient").attr("id", "greenGradient");
            setGradientStops(greenGradient, "#6ec664", "#3f9c35");
            const redStroke = "#8b0000";
            const greenStroke = "#37892f";

            const dropShadowFilter = defs.append("filter").attr("id", "shadow").attr("width", 16).attr("height", 16).attr("x", -3);
            dropShadowFilter.append("feDropShadow").attr("dx", 0).attr("dy", 2).attr("stdDeviation", 1).attr("flood-opacity", 0.3);

            const rootElement = Object.assign({}, JSON.parse(JSON.stringify(this.treeData)));
            d3.layout.hierarchy().children(d => d.checks)(rootElement);

            // Styling props
            const leftMargin = 50;

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
                .style("fill", d => d.status === 'UP' ? 'url(#greenGradient)' : 'url(#redGradient)')
                .style("stroke", d => d.status === 'UP' ? greenStroke : redStroke);

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

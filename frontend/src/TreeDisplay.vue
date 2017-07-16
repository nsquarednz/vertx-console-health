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
        filter: url(#shadow);
        cursor: pointer;
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
    beforeMount() {
        this.disableDraw = false;
        this.visibilityFn = () => {
            if (document.hidden) {
                this.disableDraw = true;
            } else {
                this.disableDraw = false;
                this.drawTree();
            }
        };
        document.addEventListener('visibilitychange', this.visibilityFn, false);
    },
    beforeDestroy() {
        document.removeEventListener('visibilitychange', this.visibilityFn, false);
    },
    watch: {
        treeData(val) {
            const dataAsString = JSON.stringify(this.treeData);
            if (dataAsString === this.lastDataString) {
                return;
            }
            this.lastDataString = dataAsString;
            const rootElement = JSON.parse(dataAsString);
            d3.layout.hierarchy().children(d => d.checks)(rootElement);
            this.rootElement = rootElement;
            if (!this.disableDraw) {
                this.drawTree();
            }
        }
    },
    methods: {
        drawTree() {
            const container = this.$el;
            const sel = d3.select(container);
            const width = container.clientWidth, height = container.clientHeight;

            const tree = d3.layout.tree().size([height, width]);
            const diagonal = d3.svg.diagonal().projection(d => [d.y, d.x]);

            const redStroke = "#8b0000";
            const greenStroke = "#37892f";

            let treeSvg = sel.select('svg');
            if (treeSvg.empty()) {
                treeSvg = sel.attr('preserveAspectRatio', 'none').append('svg');

                const defs = treeSvg.append("defs");
                const redGradient = defs.append("linearGradient").attr("id", "redGradient");
                setGradientStops(redGradient, "#c00", "#a30000");
                const greenGradient = defs.append("linearGradient").attr("id", "greenGradient");
                setGradientStops(greenGradient, "#6ec664", "#3f9c35");

                const dropShadowFilter = defs.append("filter").attr("id", "shadow").attr("width", 16).attr("height", 16).attr("x", -3);
                dropShadowFilter.append("feDropShadow").attr("dx", 0).attr("dy", 2).attr("stdDeviation", 1).attr("flood-opacity", 0.3);
            } else {
                treeSvg.selectAll('.redraw').remove();
            }

            const leftMargin = 50;

            let i = 0;
            const nodes = tree.nodes(this.rootElement).reverse(),
                links = tree.links(nodes);
            nodes.forEach(d => d.y = d.depth * 120);

            const node = treeSvg.selectAll("g.node")
                .data(nodes, d => d.uid || (d.uid = ++i));

            const nodeEnter = node.enter().append("g")
                .attr("class", "node redraw")
                .attr("transform", d => "translate(" + (d.y + leftMargin) + "," + d.x + ")");
            nodeEnter.append("circle")
                .attr("r", 10)
                .style("fill", d => d.status === 'UP' ? 'url(#greenGradient)' : 'url(#redGradient)')
                .style("stroke", d => d.status === 'UP' ? greenStroke : redStroke)
                .on("click", d => console.log(d));
            nodeEnter.append("text")
                .attr("x", d => d.children ? -16 : 16)
                .attr("dy", ".35em")
                .attr("text-anchor", d => d.children ? "end" : "start")
                .text(d => d.id)
                .style("fill-opacity", 1);

            const link = treeSvg.selectAll("path.link")
                .data(links, d => d.target.uid);
            link.enter().insert("path", "g")
                .attr("class", "link redraw")
                .attr("transform", d => "translate(" + leftMargin + ")")
                .attr("d", diagonal);
        }
    }
}
</script>

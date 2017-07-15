<template>
    <div v-resize:throttle="onResize">
        {{ treeData }}
    </div>
</template>


<script>
import resize from 'vue-resize-directive';

export default {
    props: {
        treeData: Object
    },
    directives: {
        resize
    },
    methods: {
        onResize() {
            console.log("resized")
        }
    }
}
</script>

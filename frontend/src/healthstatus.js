let url = null;
let callbacks = [];
let lastStatus = null;

const updateStatuses = () => axios.get(url,
    { validateStatus: status => (status >= 200 && status < 300) || status === 500 || status === 503 })
    .then(response => response.data)
    .then(healthJson => {
        healthJson.id = 'root';
        healthJson.status = healthJson.outcome;

        function setAddress(parent, node) {
            node.address = parent ? parent.address + '/' + node.id : node.id;
            if (node.checks) {
                for (let checkObj of node.checks) {
                    setAddress(node, checkObj);
                }
            }
        }

        setAddress(null, healthJson);

        lastStatus = healthJson;
        for (let cb of callbacks) {
            cb(Object.assign({}, lastStatus), 200);
        }
    });

export default {
    initialize(endpoint) {
        url = window.location.pathname + endpoint;
        updateStatuses();
        setInterval(updateStatuses, 1000);
    },
    addCallback(cb) {
        callbacks.push(cb);
        if (lastStatus !== null) {
            cb(Object.assign({}, lastStatus));
        }
    },
    removeCallback(cb) {
        callbacks = callbacks.filter(el => el != cb);
    }
}
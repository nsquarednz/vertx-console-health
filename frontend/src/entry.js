import Health from './Health.vue';

if (!window.vertxConsoleRoutes) window.vertxConsoleRoutes = [];
window.vertxConsoleRoutes.push({
    path: '/health',
    component: Health
});

import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'
import Dashboard from '@/views/Dashboard.vue';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import BoardDetails from '@/views/BoardDetails.vue';


const routes = [
    {
        path: '/',
        name: 'dashboard',
        component: Dashboard,
        meta: { requiresAuth: true }
    },
    {
        path: '/board/:id',
        name: 'board-details',
        component: BoardDetails,
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'login',
        component: Login
    },
    {
        path: '/register',
        name: 'register',
        component: Register
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to) => {
    const auth = useAuthStore();

    if (!auth.initialized) {
        await auth.restoreSession();
    }

    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        return '/login';
    }

    if ((to.name === 'login' || to.name === 'register') && auth.isAuthenticated) {
        return '/';
    }
});

export { router };
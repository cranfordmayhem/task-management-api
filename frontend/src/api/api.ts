import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  withCredentials: true,
});

let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Attach access token to each request
api.interceptors.request.use(config => {
  const auth = useAuthStore();
  if (auth.accessToken) {
    config.headers['Authorization'] = `Bearer ${auth.accessToken}`;
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  async (error) => {
    const original = error.config;

    // Fix typo here
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;

      if (isRefreshing) {
        return new Promise(function (resolve, reject) {
          failedQueue.push({ resolve, reject });
        })
          .then(() => api(original))
          .catch(err => Promise.reject(err));
      }

      isRefreshing = true;
      try {
        // Call refresh token endpoint
        const auth = useAuthStore();
        const res = await api.post('/auth/refresh');
        auth.setAccessToken(res.data.accessToken); // store new token
        processQueue(null, res.data.accessToken);
        return api(original);
      } catch (err) {
        processQueue(err, null);
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;

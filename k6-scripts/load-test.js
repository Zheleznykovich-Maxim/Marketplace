import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 500 },   // разогрев до 500 виртуальных пользователей
        { duration: '1m', target: 1000 },   // рост до 1000
        { duration: '1m', target: 2000 },   // рост до 2000
        { duration: '2m', target: 2000 },   // держим 2000
        { duration: '30s', target: 0 },     // спад
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],      // <1% ошибок
        http_req_duration: ['p(95)<500'],    // 95% запросов < 500ms
    },
};

export default function () {
    // Теперь URL на nginx, который балансирует между 3 репликами
    const url = 'http://nginx:80/api/orders';
    const payload = JSON.stringify({ productId: 1, qty: 1 });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(0.01);
}

import http from 'k6/http';
import { check } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800']
  }
};

export default function () {
  const health = http.get(`${baseUrl}/api/v1/health`);
  check(health, {
    'health status 200': (r) => r.status === 200
  });

  const threats = http.get(`${baseUrl}/api/v1/threats?page=0&size=10`);
  check(threats, {
    'threats status 200': (r) => r.status === 200
  });
}

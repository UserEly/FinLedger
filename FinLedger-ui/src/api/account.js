import api from './index'

export const accountApi = {
  getAll: () => api.get('/accounts'),
  getById: (id) => api.get(`/accounts/${id}`),
  getByCategory: (category) => api.get(`/accounts/category/${category}`),
  getRoot: () => api.get('/accounts/root'),
  getChildren: (parentId) => api.get(`/accounts/parent/${parentId}`),
  create: (data) => api.post('/accounts', data),
  update: (id, data) => api.put(`/accounts/${id}`, data),
  delete: (id) => api.delete(`/accounts/${id}`)
}



// ===== 演示模式 — 全部 mock 数据，不依赖后端 =====

const now = '2025-06-30 10:30:00'

// ---- 工具函数 ----
const page = (list, pageNum = 1, pageSize = 10) => ({
  records: list.slice((pageNum - 1) * pageSize, pageNum * pageSize),
  total: list.length, size: pageSize, current: pageNum, pages: Math.ceil(list.length / pageSize)
})

// ==================== 管理员端 Mock ====================

// 首页统计
export const homeStats = {
  userCount: 128, productCount: 56, orderCount: 389,
  pendingCommentCount: 12, pendingFarmerCount: 5,
  onlineProductCount: 42, totalSales: 128600
}

// 用户列表
export const users = Array.from({ length: 15 }, (_, i) => ({
  id: 1000000000000001 + i,
  username: `user_${i + 1}`,
  nickname: `用户${i + 1}`,
  phone: `1380000${String(i).padStart(4, '0')}`,
  email: `user${i + 1}@example.com`,
  userType: i < 2 ? 3 : i < 6 ? 2 : 1,
  status: i === 12 ? 0 : 1,
  createTime: `2025-0${(i % 9) + 1}-${String((i % 28) + 1).padStart(2, '0')} 10:00:00`
}))

// 分类
export const categories = [
  { id: 1, parentId: null, categoryName: '蔬菜', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (7).png', sort: 1, status: 1 },
  { id: 2, parentId: null, categoryName: '水果', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (8).png', sort: 2, status: 1 },
  { id: 3, parentId: null, categoryName: '粮食', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (10).png', sort: 3, status: 1 },
  { id: 4, parentId: null, categoryName: '肉类', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (11).png', sort: 4, status: 1 },
  { id: 5, parentId: null, categoryName: '茶叶', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (12).png', sort: 5, status: 1 },
  { id: 6, parentId: null, categoryName: '水产', categoryIcon: '', categoryImage: '/images/categories/电商平台农产品视觉素材需求 (13).png', sort: 6, status: 1 },
  { id: 11, parentId: 1, categoryName: '叶菜类', categoryIcon: '', categoryImage: '', sort: 1, status: 1 },
  { id: 12, parentId: 1, categoryName: '茄果类', categoryIcon: '', categoryImage: '', sort: 2, status: 1 },
  { id: 21, parentId: 2, categoryName: '浆果类', categoryIcon: '', categoryImage: '', sort: 1, status: 1 },
  { id: 22, parentId: 2, categoryName: '核果类', categoryIcon: '', categoryImage: '', sort: 2, status: 1 },
]

// 商品
export const products = [
  { id: 9000000000000001, productNo: 'P20250001', productName: '有机番茄', categoryId: 12, farmerId: 6000000000000001, mainImage: '/images/products/电商平台农产品视觉素材需求 (3).png', price: 12.80, originalPrice: 19.90, stock: 500, sales: 1280, unit: 'kg', weight: '2.5kg', originPlace: '湖北武汉黄陂', description: '有机种植番茄，自然成熟，不使用化学农药和化肥。果实饱满多汁，酸甜可口，富含番茄红素和维生素C。', isRecommend: 1, isNew: 0, isHot: 1, status: 1, auditStatus: 1, traceId: 1300000000000001, viewCount: 3200, commentCount: 86, goodRate: 98 },
  { id: 9000000000000002, productNo: 'P20250002', productName: '有机黄瓜', categoryId: 11, farmerId: 6000000000000001, mainImage: '/images/products/电商平台农产品视觉素材需求 (4).png', price: 8.80, originalPrice: 13.50, stock: 300, sales: 960, unit: 'kg', weight: '2.5kg', originPlace: '山东潍坊寿光', description: '新鲜采摘有机黄瓜，脆嫩爽口。全程绿色种植，不催熟不打蜡，带刺顶花，自然清香。', isRecommend: 1, isNew: 1, isHot: 0, status: 1, auditStatus: 1, traceId: 1300000000000002, viewCount: 2100, commentCount: 52, goodRate: 96 },
  { id: 9000000000000003, productNo: 'P20250003', productName: '有机大米', categoryId: 3, farmerId: 6000000000000002, mainImage: '/images/products/电商平台农产品视觉素材需求 (5).png', price: 45.00, originalPrice: 68.00, stock: 800, sales: 3200, unit: '袋', weight: '5kg', originPlace: '黑龙江五常', description: '五常有机大米，颗粒饱满晶莹，煮饭香气四溢，口感软糯弹牙。通过有机认证，产地直供。', isRecommend: 1, isNew: 0, isHot: 1, status: 1, auditStatus: 1, traceId: 1300000000000003, viewCount: 8900, commentCount: 256, goodRate: 99 },
  { id: 9000000000000004, productNo: 'P20250004', productName: '红富士苹果', categoryId: 22, farmerId: 6000000000000003, mainImage: '/images/products/电商平台农产品视觉素材需求 (6).png', price: 29.90, originalPrice: 39.90, stock: 600, sales: 1800, unit: '箱', weight: '2.5kg', originPlace: '山东烟台', description: '烟台红富士苹果，脆甜多汁，果肉细腻。产地直发，新鲜采摘，每个苹果都经过严格筛选。', isRecommend: 1, isNew: 0, isHot: 1, status: 1, auditStatus: 1, traceId: null, viewCount: 5600, commentCount: 128, goodRate: 97 },
  { id: 9000000000000005, productNo: 'P20250005', productName: '有机草莓', categoryId: 21, farmerId: 6000000000000004, mainImage: '/images/products/电商平台农产品视觉素材需求 (9).png', price: 39.90, originalPrice: 55.00, stock: 200, sales: 650, unit: '盒', weight: '500g', originPlace: '四川成都', description: '奶油草莓，香甜浓郁，入口即化。有机种植，蜜蜂授粉，不使用膨大剂，安全放心。', isRecommend: 1, isNew: 1, isHot: 1, status: 1, auditStatus: 1, traceId: null, viewCount: 4200, commentCount: 98, goodRate: 95 },
  { id: 9000000000000006, productNo: 'P20250006', productName: '散养土鸡蛋', categoryId: 4, farmerId: 6000000000000005, mainImage: '', price: 58.00, originalPrice: 78.00, stock: 150, sales: 420, unit: '箱', weight: '30枚', originPlace: '安徽黄山', description: '林间散养土鸡蛋，蛋黄饱满色泽金黄，蛋白浓稠。每枚鸡蛋都可追溯到具体养殖批次。', isRecommend: 1, isNew: 0, isHot: 0, status: 1, auditStatus: 1, traceId: null, viewCount: 1800, commentCount: 42, goodRate: 94 },
  { id: 9000000000000007, productNo: 'P20250007', productName: '明前龙井茶', categoryId: 5, farmerId: 6000000000000006, mainImage: '', price: 188.00, originalPrice: 268.00, stock: 80, sales: 230, unit: '盒', weight: '250g', originPlace: '浙江杭州', description: '西湖龙井核心产区，明前采摘，手工炒制。色泽翠绿，香气清雅，滋味甘醇。', isRecommend: 0, isNew: 1, isHot: 0, status: 1, auditStatus: 1, traceId: null, viewCount: 3500, commentCount: 68, goodRate: 97 },
  { id: 9000000000000008, productNo: 'P20250008', productName: '有机三文鱼', categoryId: 6, farmerId: 6000000000000007, mainImage: '', price: 128.00, originalPrice: 158.00, stock: 50, sales: 180, unit: '份', weight: '500g', originPlace: '山东青岛', description: '深海养殖三文鱼，冷链直达。肉质鲜嫩，纹理清晰，刺身级品质。', isRecommend: 0, isNew: 0, isHot: 0, status: 0, auditStatus: 1, traceId: null, viewCount: 1200, commentCount: 36, goodRate: 92 },
]

// 轮播图
export const banners = [
  { id: 1, title: '新鲜溯源 · 健康直达', imageUrl: '/images/banners/电商平台农产品视觉素材需求.png', linkType: 0, linkId: null, linkUrl: '', sort: 1, status: 1, remark: '首页主横幅' },
  { id: 2, title: '产地直供 · 新鲜到家', imageUrl: '/images/banners/电商平台农产品视觉素材需求 (1).png', linkType: 0, linkId: null, linkUrl: '', sort: 2, status: 1, remark: '促销活动' },
  { id: 3, title: '全程可追溯 · 品质有保障', imageUrl: '/images/banners/电商平台农产品视觉素材需求 (2).png', linkType: 2, linkId: 2, linkUrl: '', sort: 3, status: 1, remark: '溯源专题' },
]

// 订单
const orderStatuses = [
  { status: 0, text: '待付款' }, { status: 1, text: '待发货' },
  { status: 2, text: '已发货' }, { status: 3, text: '已完成' },
  { status: 4, text: '已取消' }, { status: 5, text: '售后中' }
]
export const orders = Array.from({ length: 20 }, (_, i) => {
  const s = i < 3 ? 1 : i < 6 ? 2 : i < 15 ? 3 : i < 18 ? 4 : i === 18 ? 0 : 5
  return {
    id: 8000000000000001 + i,
    orderNo: `ORD202506${String(i + 1).padStart(4, '0')}`,
    userId: 1000000000000003 + (i % 10),
    farmerId: 6000000000000001 + (i % 7),
    totalAmount: 128 + i * 15,
    payAmount: 108 + i * 12,
    freightAmount: 0,
    orderStatus: s,
    payType: '微信支付',
    receiverName: `收货人${i + 1}`,
    receiverPhone: `1390000${String(i).padStart(4, '0')}`,
    receiverProvince: '湖北省',
    receiverCity: '武汉市',
    receiverDistrict: '洪山区',
    receiverAddress: `珞喻路${i + 1}号`,
    logisticsNo: s >= 2 ? `SF${100000 + i}` : null,
    logisticsCompany: s >= 2 ? '顺丰速运' : null,
    orderRemark: i === 5 ? '请尽快发货' : '',
    adminRemark: ''
  }
})

// 订单明细
export const orderItems = (orderId) => {
  const idx = Number(orderId) - 8000000000000001
  const product = products[idx % products.length]
  return [{
    id: 7000000000000001 + idx,
    orderId: Number(orderId),
    orderNo: `ORD202506${String(idx + 1).padStart(4, '0')}`,
    productId: product.id,
    productName: product.productName,
    productImage: product.mainImage,
    price: product.price,
    originalPrice: product.originalPrice,
    quantity: 2,
    totalAmount: product.price * 2,
    unit: product.unit,
    specInfo: product.weight,
    traceId: product.traceId,
    isComment: idx < 10 ? 1 : 0
  }]
}

// 订单日志
export const orderLogs = (orderId) => {
  const idx = Number(orderId) - 8000000000000001
  const s = orders[idx] ? orders[idx].orderStatus : 0
  return [
    { id: 1, orderId: Number(orderId), orderNo: `ORD202506${String(idx + 1).padStart(4, '0')}`, orderStatus: 0, operatorType: 2, operatorId: 1000000000000003, remark: '用户下单', createTime: `2025-06-${String((idx % 28) + 1).padStart(2, '0')} 09:00:00` },
    { id: 2, orderId: Number(orderId), orderNo: `ORD202506${String(idx + 1).padStart(4, '0')}`, orderStatus: 1, operatorType: 1, operatorId: 0, remark: '支付成功', createTime: `2025-06-${String((idx % 28) + 1).padStart(2, '0')} 09:05:00` },
    ...(s >= 2 ? [{ id: 3, orderId: Number(orderId), orderNo: `ORD202506${String(idx + 1).padStart(4, '0')}`, orderStatus: 2, operatorType: 3, remark: '管理员发货', createTime: `2025-06-${String((idx % 28) + 1).padStart(2, '0')} 14:00:00` }] : []),
    ...(s >= 3 ? [{ id: 4, orderId: Number(orderId), orderNo: `ORD202506${String(idx + 1).padStart(4, '0')}`, orderStatus: 3, operatorType: 1, remark: '签收完成', createTime: `2025-06-${String((idx % 28) + 2).padStart(2, '0')} 10:00:00` }] : []),
  ]
}

// 支付记录
export const payments = orders.filter(o => o.orderStatus !== 0).map((o, i) => ({
  id: 1100000000000001 + i,
  orderId: o.id, orderNo: o.orderNo, userId: o.userId,
  paymentNo: `PAY202506${String(i + 1).padStart(4, '0')}`,
  payType: '微信支付', payAmount: o.payAmount, payStatus: o.orderStatus >= 3 ? 1 : o.orderStatus === 5 ? 2 : 1,
  payTime: `2025-06-${String((i % 28) + 1).padStart(2, '0')} 09:05:00`,
  thirdPartyNo: `WX${100000 + i}`,
  refundAmount: o.orderStatus === 5 ? o.payAmount : 0,
  refundTime: o.orderStatus === 5 ? `2025-06-${String((i % 28) + 3).padStart(2, '0')} 15:00:00` : null,
  refundReason: o.orderStatus === 5 ? '商品破损' : null
}))

// 售后工单
export const afterSales = [
  { id: 1200000000000001, afterSalesNo: 'AS2025060001', orderId: 8000000000000019, orderNo: 'ORD2025060020', userId: 1000000000000008, productName: '有机番茄', afterSalesType: 2, reason: '商品破损', description: '收到时包装破损，番茄有压坏', refundAmount: 12.80, status: 0, adminRemark: '', applyTime: '2025-06-28 15:00:00', auditTime: null },
  { id: 1200000000000002, afterSalesNo: 'AS2025060002', orderId: 8000000000000010, orderNo: 'ORD2025060011', userId: 1000000000000005, productName: '有机大米', afterSalesType: 1, reason: '不想要了', description: '买多了，申请退款', refundAmount: 45.00, status: 1, adminRemark: '同意退款', applyTime: '2025-06-27 10:00:00', auditTime: '2025-06-27 16:00:00' },
  { id: 1200000000000003, afterSalesNo: 'AS2025060003', orderId: 8000000000000005, orderNo: 'ORD2025060006', userId: 1000000000000003, productName: '有机草莓', afterSalesType: 3, reason: '发错货了', description: '收到的草莓规格不对', refundAmount: 0, status: 3, adminRemark: '已换货完成', applyTime: '2025-06-20 09:00:00', auditTime: '2025-06-20 14:00:00' },
]

// 客服聊天
export const csLogs = (afterSalesId) => {
  const logs = {
    '1200000000000001': [
      { id: 1, afterSalesId: 1200000000000001, orderId: 8000000000000019, userId: 1000000000000008, operatorType: 1, operatorName: '用户', msgType: 1, content: '收到货发现包装破了，番茄被压坏了', imageUrls: '', createTime: '2025-06-28 15:05:00' },
      { id: 2, afterSalesId: 1200000000000001, orderId: 8000000000000019, userId: 1000000000000008, operatorType: 1, operatorName: '用户', msgType: 1, content: '能退款吗？', imageUrls: '', createTime: '2025-06-28 15:06:00' },
    ],
    '1200000000000002': [
      { id: 3, afterSalesId: 1200000000000002, orderId: 8000000000000010, userId: 1000000000000005, operatorType: 1, operatorName: '用户', msgType: 1, content: '买多了，想退一袋', imageUrls: '', createTime: '2025-06-27 10:02:00' },
      { id: 4, afterSalesId: 1200000000000002, orderId: 8000000000000010, userId: 1000000000000005, operatorType: 2, operatorName: '客服小王', msgType: 1, content: '好的，已为您处理退款申请', imageUrls: '', createTime: '2025-06-27 16:05:00' },
    ]
  }
  return logs[String(afterSalesId)] || []
}

// 评论
export const comments = Array.from({ length: 18 }, (_, i) => ({
  id: 1400000000000001 + i,
  userId: 1000000000000003 + (i % 10),
  productId: products[i % 8].id,
  productName: products[i % 8].productName,
  content: i < 12 ? `产品质量很好，${i % 3 === 0 ? '非常新鲜！' : i % 3 === 1 ? '物流很快！' : '包装很用心！'}还会回购的。` : `感觉一般，${i % 2 === 0 ? '物流有点慢' : '包装可以更好'}`,
  rating: i < 12 ? 5 : i < 15 ? 4 : 3,
  status: i < 13 ? 1 : i < 15 ? 2 : 0,
  createTime: `2025-06-${String((i % 28) + 1).padStart(2, '0')} 12:00:00`
}))

// ==================== 溯源+农户端 Mock ====================

export const traceData = {
  traceability: {
    id: 1300000000000001,
    traceCode: 'TR202504010001',
    batchNo: 'B20250401',
    productName: '有机番茄',
    originPlace: '湖北武汉黄陂',
    farmName: '张大农户有机农场',
    responsiblePerson: '张三',
    plantingDate: '2025-02-15',
    expectedHarvestDate: '2025-04-10'
  },
  planting: {
    id: 1, traceId: 1300000000000001,
    plantingDate: '2025-02-15', plantingMethod: '大棚育苗移栽',
    plantingArea: '5.0亩', soilType: '沙壤土', baseName: '黄陂有机种植基地1号大棚',
    responsiblePerson: '张三', remark: '选用优质番茄苗，底肥使用有机肥'
  },
  fertilizers: [
    { id: 1, traceId: 1300000000000001, fertilizerName: '有机堆肥', amount: 500, amountUnit: 'kg', fertilizeMethod: '沟施', fertilizeDate: '2025-02-10', operator: '张三', remark: '底肥' },
    { id: 2, traceId: 1300000000000001, fertilizerName: '沼液', amount: 200, amountUnit: 'L', fertilizeMethod: '滴灌追肥', fertilizeDate: '2025-03-15', operator: '李四', remark: '追肥1次' },
  ],
  pesticides: [
    { id: 1, traceId: 1300000000000001, pesticideName: '苏云金杆菌（生物制剂）', amount: 100, amountUnit: 'g', sprayDate: '2025-03-20', safetyInterval: 15, operator: '张三', remark: '防治菜青虫，低毒生物农药' },
  ],
  irrigations: [
    { id: 1, traceId: 1300000000000001, waterSource: '地下水', amount: 10, amountUnit: 'm³', irrigateMethod: '滴灌', irrigateDate: '2025-02-20', operator: '李四', remark: '定植后第一次灌溉' },
    { id: 2, traceId: 1300000000000001, waterSource: '地下水', amount: 8, amountUnit: 'm³', irrigateMethod: '滴灌', irrigateDate: '2025-03-10', operator: '李四', remark: '生长期灌溉' },
  ],
  breedings: [],
  harvest: {
    id: 1, traceId: 1300000000000001, harvestDate: '2025-04-10', amount: 800, amountUnit: 'kg',
    harvestMethod: '人工采摘', operator: '张三', remark: '清晨采摘，保证新鲜度'
  },
  processing: [
    { id: 1, traceId: 1300000000000001, processDate: '2025-04-10', processMethod: '分拣+包装', companyName: '黄陂农产品加工中心', operator: '王五', remark: '按大小分级，真空包装' }
  ],
  inspection: [
    { id: 1, traceId: 1300000000000001, inspectDate: '2025-04-11', inspectOrg: '武汉市农产品质量检测中心', result: '合格', reportNo: 'WHJC-2025-0411-001', remark: '农残检测未检出，符合有机标准' }
  ],
  storage: {
    id: 1, traceId: 1300000000000001, storageDate: '2025-04-11', storageCondition: '冷藏 0-4°C', location: '黄陂冷链中心A区', responsiblePerson: '赵六', remark: '入库前预冷处理'
  },
  logistics: {
    id: 1, traceId: 1300000000000001, companyName: '顺丰冷链', logisticsNo: 'SF1234567890',
    shipDate: '2025-04-12', originAddress: '湖北武汉黄陂冷链中心', destinationAddress: '客户指定地址'
  },
  logisticsTracks: [
    { id: 1, logisticsId: 1, status: '已揽收', description: '快递员已取件', trackTime: '2025-04-12 08:30:00' },
    { id: 2, logisticsId: 1, status: '运输中', description: '快件已到达武汉分拣中心', trackTime: '2025-04-12 14:00:00' },
    { id: 3, logisticsId: 1, status: '派送中', description: '快递员正在派送', trackTime: '2025-04-13 09:00:00' },
    { id: 4, logisticsId: 1, status: '已签收', description: '已签收', trackTime: '2025-04-13 10:30:00' },
  ],
  images: [
    { id: 1, traceId: 1300000000000001, imageUrl: '/images/trace/电商平台农产品视觉素材需求 (14).png', imageType: '种植', sort: 1 },
    { id: 2, traceId: 1300000000000001, imageUrl: '/images/trace/电商平台农产品视觉素材需求 (15).png', imageType: '收获', sort: 2 },
    { id: 3, traceId: 1300000000000001, imageUrl: '/images/trace/电商平台农产品视觉素材需求 (16).png', imageType: '检测', sort: 3 },
    { id: 4, traceId: 1300000000000001, imageUrl: '/images/trace/电商平台农产品视觉素材需求 (17).png', imageType: '物流', sort: 4 },
    { id: 5, traceId: 1300000000000001, imageUrl: '/images/trace/电商平台农产品视觉素材需求 (18).png', imageType: '加工', sort: 5 },
  ]
}

// 农户
export const farmers = [
  { id: 6000000000000001, userId: 1000000000000005, farmerName: '张大农户有机农场', contactPerson: '张三', contactPhone: '13700000001', province: '湖北省', city: '武汉市', district: '黄陂区', address: '黄陂区王家河镇农场路8号', farmArea: 150.00, mainProducts: '有机番茄、黄瓜、青菜', auditStatus: 1, level: 5, score: 4.90, salesCount: 2800 },
  { id: 6000000000000002, userId: 1000000000000006, farmerName: '五常大米种植基地', contactPerson: '李四', contactPhone: '13700000002', province: '黑龙江省', city: '哈尔滨市', district: '五常市', address: '五常市龙凤山镇', farmArea: 500.00, mainProducts: '五常有机大米', auditStatus: 1, level: 4, score: 4.80, salesCount: 5200 },
  { id: 6000000000000003, userId: 1000000000000007, farmerName: '王五家庭农场', contactPerson: '王五', contactPhone: '13700000003', province: '山东省', city: '烟台市', district: '牟平区', address: '牟平区龙泉镇果园路12号', farmArea: 80.00, mainProducts: '烟台红富士苹果', auditStatus: 1, level: 3, score: 4.70, salesCount: 1800 },
]

// ==================== 路由匹配器 ====================

export function matchAdmin(url, params) {
  const u = url.split('?')[0]

  // 首页统计
  if (u.includes('/statistics/home')) return homeStats

  // 用户管理
  if (u.includes('/user/list')) return page(users, params?.pageNum, params?.pageSize)
  if (u.match(/\/user\/\d+$/)) return users[0]
  if (u.includes('/user/change-status')) return null

  // 分类管理
  if (u.includes('/category/list')) return page(categories, params?.pageNum, params?.pageSize)
  if (u.includes('/category/tree')) return categories
  if (u.includes('/category/first-level')) return categories.filter(c => !c.parentId)
  if (u.match(/\/category\/\d+$/)) return categories[0]
  if (u.includes('/category/change-status')) return null

  // 商品管理
  if (u.includes('/product/list')) return page(products, params?.pageNum, params?.pageSize)
  if (u.match(/\/product\/\d+$/)) return products[0]
  if (u.includes('/product/change-status') || u.includes('/product/change-recommend')) return null

  // 商品图片
  if (u.includes('/product-image/list')) return [{ id: 1, productId: 9000000000000001, imageUrl: '/images/products/电商平台农产品视觉素材需求 (3).png', imageType: 1, sort: 1 }]

  // 轮播图
  if (u.includes('/banner/list')) return page(banners, params?.pageNum, params?.pageSize)
  if (u.match(/\/banner\/\d+$/)) return banners[0]
  if (u.includes('/banner/change-status')) return null

  // 订单管理
  if (u.includes('/order/list')) return page(orders, params?.pageNum, params?.pageSize)
  if (u.match(/\/order\/delivery/)) return null
  if (u.match(/\/order\/change-status/)) return null
  if (u.match(/\/order\/\d+$/)) {
    const id = u.match(/\/order\/(\d+)/)?.[1]
    return orders.find(o => o.id === Number(id)) || orders[0]
  }

  // 订单明细
  if (u.includes('/order-item/list')) {
    const id = u.match(/\/order-item\/list\/(\d+)/)?.[1]
    return orderItems(id || 8000000000000001)
  }
  if (u.match(/\/order-item\/\d+$/)) return orderItems(8000000000000001)[0]

  // 订单日志
  if (u.includes('/order-log/list')) {
    const id = u.match(/\/order-log\/list\/(\d+)/)?.[1]
    return orderLogs(id || 8000000000000001)
  }

  // 支付记录
  if (u.includes('/payment/list')) return page(payments, params?.pageNum, params?.pageSize)
  if (u.match(/\/payment\/by-order/)) return payments[0]
  if (u.match(/\/payment\/\d+$/)) return payments[0]

  // 售后工单
  if (u.includes('/after-sales/list')) return page(afterSales, params?.pageNum, params?.pageSize)
  if (u.match(/\/after-sales\/audit/) || u.match(/\/after-sales\/close/)) return null
  if (u.match(/\/after-sales\/\d+$/)) {
    const id = u.match(/\/after-sales\/(\d+)/)?.[1]
    return afterSales.find(a => a.id === Number(id)) || afterSales[0]
  }

  // 客服记录
  if (u.includes('/cs-log/list')) {
    const id = u.match(/\/cs-log\/list\/(\d+)/)?.[1]
    return csLogs(id || 1200000000000001)
  }

  // 评论审核
  if (u.includes('/comment/list')) return page(comments, params?.pageNum, params?.pageSize)
  if (u.match(/\/comment\/approve/) || u.match(/\/comment\/reject/)) return null
  if (u.match(/\/comment\/\d+$/)) return comments[0]

  // 默认
  return null
}

export function matchTrace(url, params) {
  const u = url.split('?')[0]

  // 溯源扫码
  if (u.includes('/trace/scan/')) return traceData
  if (u.includes('/trace/detail/')) return traceData
  if (u.includes('/trace/list')) return page([traceData.traceability], params?.pageNum, params?.pageSize)

  // 各子模块
  if (u.includes('/trace/planting/')) return traceData.planting
  if (u.includes('/trace/fertilizer/list')) return traceData.fertilizers
  if (u.includes('/trace/pesticide/list')) return traceData.pesticides
  if (u.includes('/trace/irrigation/list')) return traceData.irrigations
  if (u.includes('/trace/breeding/list')) return traceData.breedings
  if (u.includes('/trace/harvest/get')) return traceData.harvest
  if (u.includes('/trace/processing/list')) return traceData.processing
  if (u.includes('/trace/inspection/list')) return traceData.inspection
  if (u.includes('/trace/storage/get')) return traceData.storage
  if (u.includes('/trace/logistics/tracks')) return traceData.logisticsTracks
  if (u.includes('/trace/logistics/')) return traceData.logistics
  if (u.includes('/trace/image/list')) return traceData.images

  // 农户
  if (u.includes('/farmer/detail')) return farmers[0]
  if (u.includes('/farmer/info')) return farmers[0]
  if (u.includes('/farmer/auth-info')) return { auditStatus: 1, remark: '已认证' }
  if (u.includes('/farmer/list')) return page(farmers, params?.pageNum, params?.pageSize)

  return null
}

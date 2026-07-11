export function maskPhone(p) { return p ? p.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '' }
export function maskEmail(e) { return e ? e.replace(/(.{2}).*(@.*)/, '$1***$2') : '' }
export function maskAddress(a) { return a ? a.replace(/(.{4}).*(.{2})/, '$1****$2') : '' }
export function maskIdCard(i) { return i ? i.replace(/(\d{4})\d+(\d{4})/, '$1**********$2') : '' }
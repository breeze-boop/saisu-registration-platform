package com.mishi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mishi.common.BusinessException;
import com.mishi.domain.Voucher;
import com.mishi.dto.VoucherDto;
import com.mishi.mapper.VoucherMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VoucherQueryService {
    private final VoucherMapper voucherMapper;
    public VoucherQueryService(VoucherMapper voucherMapper) { this.voucherMapper = voucherMapper; }
    public List<VoucherDto> list() { return voucherMapper.selectList(new LambdaQueryWrapper<Voucher>().orderByDesc(Voucher::getId)).stream().map(this::toDto).toList(); }
    public VoucherDto get(Long id) { Voucher voucher = voucherMapper.selectById(id); if (voucher == null) throw new BusinessException("优惠券不存在"); return toDto(voucher); }
    private VoucherDto toDto(Voucher voucher) { return new VoucherDto(voucher.getId(), voucher.getShopId(), voucher.getTitle(), voucher.getPayValue(), voucher.getActualValue(), voucher.getStock(), voucher.getBeginAt(), voucher.getEndAt()); }
}

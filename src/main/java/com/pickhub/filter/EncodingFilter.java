package com.pickhub.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/** Ep moi request / response dung UTF-8 de tieng Viet khong bi loi font. */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest yeuCau, ServletResponse phanHoi, FilterChain chuoi) throws IOException, ServletException {
        yeuCau.setCharacterEncoding("UTF-8");
        phanHoi.setCharacterEncoding("UTF-8");
        chuoi.doFilter(yeuCau, phanHoi);
    }
}

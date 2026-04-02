package com.admin.controller;

import com.admin.service.SysUserService;
import com.admin.service.SysRoleService;
import com.admin.service.SysMenuService;
import com.admin.service.SysOperLogService;
import com.admin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysOperLogService logService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取各模块统计数据
        long userCount = userService.count();
        long roleCount = roleService.count();
        long menuCount = menuService.count();
        long logCount = logService.count();

        stats.put("userCount", userCount);
        stats.put("roleCount", roleCount);
        stats.put("menuCount", menuCount);
        stats.put("logCount", logCount);

        // 获取最近7天访问量数据
        List<String> dates = new ArrayList<>();
        List<Integer> visits = new ArrayList<>();
        String[] weekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : weekDays) {
            dates.add(day);
            visits.add((int) (Math.random() * 1000) + 500);
        }
        stats.put("visitDates", dates);
        stats.put("visitData", visits);

        // 获取用户分布数据
        List<Map<String, Object>> userDistribution = new ArrayList<>();
        userDistribution.add(createDistributionItem(1048, "管理员"));
        userDistribution.add(createDistributionItem(735, "编辑员"));
        userDistribution.add(createDistributionItem(580, "访客"));
        userDistribution.add(createDistributionItem(484, "其他"));
        stats.put("userDistribution", userDistribution);

        return Result.success(stats);
    }

    private Map<String, Object> createDistributionItem(int value, String name) {
        Map<String, Object> item = new HashMap<>();
        item.put("value", value);
        item.put("name", name);
        return item;
    }
}

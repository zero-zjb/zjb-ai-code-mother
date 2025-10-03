package com.zjb.zjbaicodemother.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.zjb.zjbaicodemother.common.DeleteRequest;
import com.zjb.zjbaicodemother.model.dto.app.AppAddRequest;
import com.zjb.zjbaicodemother.model.dto.app.AppAdminUpdateRequest;
import com.zjb.zjbaicodemother.model.dto.app.AppQueryRequest;
import com.zjb.zjbaicodemother.model.dto.app.AppUpdateRequest;
import com.zjb.zjbaicodemother.model.entity.App;
import com.zjb.zjbaicodemother.model.vo.AppVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author zjb
 */
public interface AppService extends IService<App> {

    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       请求
     * @return 应用 id
     */
    Long addApp(AppAddRequest appAddRequest, HttpServletRequest request);

    /**
     * 更新应用（用户只能更新自己的应用名称）
     *
     * @param appUpdateRequest 更新请求
     * @param request          请求
     * @return 更新结果
     */
    Boolean updateApp(AppUpdateRequest appUpdateRequest, HttpServletRequest request);

    /**
     * 删除应用（用户只能删除自己的应用）
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 删除结果
     */
    Boolean deleteApp(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 分页获取当前用户创建的应用列表
     *
     * @param appQueryRequest 查询请求
     * @param request         请求
     * @return 应用列表
     */
    Page<AppVO> listMyAppVOByPage(AppQueryRequest appQueryRequest, HttpServletRequest request);

    /**
     * 分页获取精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 精选应用列表
     */
    Page<AppVO> listGoodAppVOByPage(AppQueryRequest appQueryRequest);

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 更新请求
     * @return 更新结果
     */
    Boolean updateAppByAdmin(AppAdminUpdateRequest appAdminUpdateRequest);

    /**
     * 管理员分页获取应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 应用列表
     */
    Page<AppVO> listAppVOByPageByAdmin(AppQueryRequest appQueryRequest);

    /**
     * 获取应用 VO
     *
     * @param app 应用
     * @return 应用 VO
     */
    AppVO getAppVO(App app);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest 应用查询条件
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取脱敏应用列表
     *
     * @param appList
     * @return appVOList
     */
    List<AppVO> getAppVOList(List<App> appList);
}

package com.simplefanc.office.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.office.user.entity.TbUser;
import com.simplefanc.office.common.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author chenfan
 */
@Mapper
public interface TbUserDao extends BaseMapper<TbUser> {
    /**
     * @return
     */
    boolean haveRootUser();

    /**
     * @param tbUser
     * @return
     */
    @Override
    int insert(TbUser tbUser);

    /**
     * @param openId
     * @return
     */
    Integer searchIdByOpenId(String openId);

    /**
     * @param userId
     * @return
     */
    Set<String> searchUserPermissions(int userId);

    /**
     * @param userId
     * @return
     */
    TbUser searchById(int userId);

    /**
     * 查询员工的姓名和部门名称
     *
     * @param userId
     * @return
     */
    HashMap<String, String> searchNameAndDept(int userId);

    /**
     * 查询员工的入职日期
     *
     * @param userId
     * @return
     */
    String searchUserHiredate(int userId);

    /**
     * @param userId
     * @return
     */
    HashMap searchUserSummary(int userId);

    /**
     * @param keyword
     * @return
     */
    ArrayList<HashMap> searchUserGroupByDept(String keyword);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchMembers(List param);

    /**
     * @param userId
     * @return
     */
    HashMap searchUserInfo(int userId);

    Integer searchDeptMangerIdByDeptId(int deptId);
    /**
     * @param id
     * @return
     */
    Integer searchDeptManagerId(int id);

    /**
     * @return
     */
    Integer searchGmId();

    /**
     * @param param
     * @return
     */
    List<HashMap> selectUserPhotoAndName(List param);

    String searchMemberEmail(int id);

    int updateUserPhoto(@Param("userId") int userId, @Param("photoUrl") String photoUrl);

    ArrayList<UserDTO> searchAllUser();

    Integer searchCountByRoleId(Integer roleId);
}
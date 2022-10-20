package com.simplefanc.office.user.service;

import com.simplefanc.office.common.dto.UserDTO;
import com.simplefanc.office.user.entity.TbUser;

import java.util.*;

/**
 * @author chenfan
 */
public interface UserService {
    /**
     * @param registerCode
     * @param code
     * @param nickname
     * @param photo
     * @return
     */
    int registerUser(String registerCode, String code, String nickname, String photo);

    /**
     * @param userId
     * @return
     */
    Set<String> searchUserPermissions(int userId);

    /**
     * @param code
     * @return
     */
    Map<String, Object> login(String code);

    /**
     * @param userId
     * @return
     */
    TbUser searchById(int userId);

    /**
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
     * @param roleId
     * @return
     */
    ArrayList<HashMap> searchUserGroupByDept(String keyword, Long deptId, Long roleId);

    /**
     * @param param
     * @return
     */
    ArrayList<HashMap> searchMembers(List param);

    /**
     * @param param
     * @return
     */
    List<HashMap> selectUserPhotoAndName(List param);

    Integer loginByPass(String id, String password, String openId);

    String searchMemberEmail(int id);

    void updateUserPhoto(int userId, String photoUrl);

    ArrayList<UserDTO> searchAllUser();

    void resetPass(int userId, String oldPass, String newPass);

    void insertOrUpdateUserInfo(TbUser tbUser);

    void deleteUserById(Integer id);

    HashMap searchUserInfo(int userId);

    Integer searchDeptManagerId(Integer userId);

    Integer searchGmId();

    HashMap<String, String> searchNameAndDept(int userId);

    void checkValidRole(Integer id, Integer deptId, Integer roleId);
}

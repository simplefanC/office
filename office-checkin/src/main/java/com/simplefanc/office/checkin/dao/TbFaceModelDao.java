package com.simplefanc.office.checkin.dao;

import com.simplefanc.office.checkin.entity.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 维护员工人脸模型数据
 *
 * @author chenfan
 */
@Mapper
public interface TbFaceModelDao {
    /**
     * @param userId
     * @return
     */
    String searchFaceModel(int userId);

    /**
     * @param faceModel
     */
    void insert(TbFaceModel faceModel);

    /**
     * @param userId
     * @return
     */
    int deleteFaceModel(int userId);
}
package edu.scu.qz.service;

import edu.scu.qz.common.ServerResponse;
import edu.scu.qz.vo.sdo.AdminIndexSdo;

public interface IStatisticService {

    ServerResponse<AdminIndexSdo> adminIndexData();

}

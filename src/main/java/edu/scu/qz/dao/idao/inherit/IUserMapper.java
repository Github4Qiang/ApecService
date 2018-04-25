package edu.scu.qz.dao.idao.inherit;

import edu.scu.qz.dao.idao.UserMapper;
import edu.scu.qz.dao.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUserMapper extends UserMapper {

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("passwordNew") String passwordNew);

    int checkPassword(@Param("password") String password, @Param("userId") Integer userId);

    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);

    // 不包括管理员
    Integer countUser();
    // 不包括管理员
    List<User> selectUserList();

    List<User> selectUserListByRole(Integer role);
}
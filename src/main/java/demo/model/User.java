package demo.model;

import com.github.annotation.Column;
import com.github.annotation.TableAlias;
import lombok.Data;

import java.util.Date;

@Data
@TableAlias(alias = "user")
public class User {
    @Column(value = "user_id")
    private Long userId;

    @Column(value = "user_name")
    private String userName;
    
    private Long age;

    @Column(value = "rowver")
    private Date rowver;
}

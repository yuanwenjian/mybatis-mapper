package demo.model;

import com.github.annotation.Column;
import com.github.annotation.TableAlias;
import lombok.Data;

import java.util.Date;

/**
 * Created with Intellij IDEA
 * Author: xuziling
 * Date:  2019/1/22
 * Description:
 */
@Data
@TableAlias(alias = "phoneImei")
public class PhoneImei {
    @Column(value = "imei_id")
    private Long imeiId;

    @Column(value = "tenant_id")
    private Long tenantId;

    @Column(value = "imei_num")
    private String imeiNum;

    @Column(value = "record_user_id")
    private Long recoredUserId;

    @Column(value = "record_date")
    private Date recordDate;

}

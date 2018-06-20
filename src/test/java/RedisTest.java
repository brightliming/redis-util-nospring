import java.util.Properties;

import com.troila.redis.JedisInterface;
import com.troila.redis.factory.JedisFactory;
import com.troila.redis.utils.ConfigureUtils;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring-core.xml"})
public class RedisTest{
//    @Autowired
//    private JedisInterface redisUtils;
//
//    @Test
//    public void testRedis() throws Exception{
//        redisUtils.set("test","test");
//        System.out.println(redisUtils.get("test"));
//    } 
    public static void main(String[] args) {
    	new ConfigureUtils().init();
    	JedisInterface redisUtils = JedisFactory.getJedis(ConfigureUtils.getConfigs());
        redisUtils.set("test","limltest");
        System.out.println(redisUtils.get("test"));
    }

}

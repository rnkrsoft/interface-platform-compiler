
import com.rnkrsoft.platform.generator.DeviceType;
import com.rnkrsoft.platform.generator.InterfacePlatformGenerator;
import com.rnkrsoft.platform.service.DemoService;
import org.junit.Test;


/**
 * Created by rnkrsoft.com on 2019/2/3.
 */
public class InterfacePlatformCompilerTest {

    @Test
    public void testCompile1() throws Exception {
        String[] serviceClasses = new String[]{
                DemoService.class.getName()
        };
        InterfacePlatformGenerator.generate("氡氪网络科技", DeviceType.iOS, "com.rnkrsoft.demo", "./target", "com.rnkrsoft.platform.service", serviceClasses);

    }
}
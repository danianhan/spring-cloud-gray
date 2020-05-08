package cn.springcloud.gray.client.netflix.ribbon;

import cn.springcloud.gray.servernode.ServerExplainer;
import cn.springcloud.gray.servernode.ServerSpec;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.netflix.ribbon.DefaultServerIntrospector;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.util.Map;

public class RibbonServerExplainer implements ServerExplainer<Server> {

    private SpringClientFactory springClientFactory;

    public RibbonServerExplainer(SpringClientFactory springClientFactory) {
        this.springClientFactory = springClientFactory;
    }

    @Override
    public ServerSpec<Server> apply(Server server) {
        Map metadata = getServerMetadata(server.getMetaInfo().getServiceIdForDiscovery(), server);
        return ServerSpec.<Server>builder()
                .server(server)
                .instanceId(getInstaceId(server))
                .serviceId(getServiceId(server))
                .metadatas(metadata).build();
    }

    @Override
    public String getServiceId(Server server) {
        return server.getMetaInfo().getServiceIdForDiscovery();
    }

    @Override
    public String getInstaceId(Server server) {
        return server.getMetaInfo().getInstanceId();
    }

    public ServerIntrospector serverIntrospector(String serviceId) {
        if (springClientFactory == null) {
            return new DefaultServerIntrospector();
        }
        ServerIntrospector serverIntrospector = this.springClientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }

    /**
     * 获取实例的metadata信息
     *
     * @param serviceId 服务id
     * @param server    ribbon服务器(服务实例)
     * @return 服务实例的metadata信息
     */
    public Map<String, String> getServerMetadata(String serviceId, Server server) {
        return serverIntrospector(serviceId).getMetadata(server);
    }
}

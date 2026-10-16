package cn.superiormc.ultimateshop.paper.adapts.v1_21_6;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.ultimateshop.paper.adapts.VersionCodecs;

public class Paper1216Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new AttributesCodec());
        registry.register(new EquippableCodec());
    }

}

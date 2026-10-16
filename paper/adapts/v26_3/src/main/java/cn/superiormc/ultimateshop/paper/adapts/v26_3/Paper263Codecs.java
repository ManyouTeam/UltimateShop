package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.ultimateshop.paper.adapts.VersionCodecs;

public class Paper263Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new AttackAnimationCodec());
        registry.register(new InteractAnimationCodec());
        registry.register(new SwingAnimationCodec());
        registry.register(new WaxedCodec());
        registry.register(new CushionColorCodec());
        registry.register(new VillagerFoodCodec());
    }

}

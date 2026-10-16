package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.ultimateshop.paper.adapts.VersionCodecs;

public class Paper12111Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new DamageTypeCodec());
        registry.register(new KineticWeaponCodec());
        registry.register(new MinimumAttackChargeCodec());
        registry.register(new PiercingWeaponCodec());
        registry.register(new SwingAnimationCodec());
        registry.register(new UseEffectsCodec());
        registry.register(new AttackRangeCodec());
    }

}

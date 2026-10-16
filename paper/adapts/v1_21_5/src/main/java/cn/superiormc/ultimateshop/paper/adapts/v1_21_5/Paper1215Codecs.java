package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodecRegistry;
import cn.superiormc.ultimateshop.paper.adapts.VersionCodecs;

public class Paper1215Codecs implements VersionCodecs {

    @Override
    public void registerCodecs(ComponentCodecRegistry registry) {
        registry.register(new NameCodec());
        registry.register(new ItemNameCodec());
        registry.register(new LoreCodec());
        registry.register(new CustomModelDataCodec());
        registry.register(new MaxStackCodec());
        registry.register(new FoodCodec());
        registry.register(new ToolCodec());
        registry.register(new JukeboxCodec());
        registry.register(new GlowCodec());
        registry.register(new UnbreakableCodec());
        registry.register(new RarityCodec());
        registry.register(new TooltipDisplayCodec());
        registry.register(new EnchantmentsCodec());
        registry.register(new AttributesCodec());
        registry.register(new DamageCodec());
        registry.register(new MaxDamageCodec());
        registry.register(new StoredEnchantsCodec());
        registry.register(new BannerPatternsCodec());
        registry.register(new PotionCodec());
        registry.register(new PotionDurationScaleCodec());
        registry.register(new ChargedProjectilesCodec());
        registry.register(new TrimCodec());
        registry.register(new DyedColorCodec());
        registry.register(new AxolotlVariantCodec());
        registry.register(new TropicalFishCodec());
        registry.register(new SkullCodec());
        registry.register(new FireworksCodec());
        registry.register(new SuspiciousStewCodec());
        registry.register(new BundleContentsCodec());
        registry.register(new OminousBottleCodec());
        registry.register(new MusicCodec());
        registry.register(new RepairCostCodec());
        registry.register(new EnchantableCodec());
        registry.register(new GliderCodec());
        registry.register(new ItemModelCodec());
        registry.register(new TooltipStyleCodec());
        registry.register(new UseCooldownCodec());
        registry.register(new EquippableCodec());
        registry.register(new WeaponCodec());
        registry.register(new DamageResistantCodec());
        registry.register(new BlocksAttacksCodec());
        registry.register(new BreakSoundCodec());
        registry.register(new UseRemainderCodec());
        registry.register(new ConsumableCodec());
    }

}

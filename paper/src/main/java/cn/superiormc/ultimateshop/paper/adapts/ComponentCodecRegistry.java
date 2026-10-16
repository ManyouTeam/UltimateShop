package cn.superiormc.ultimateshop.paper.adapts;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentCodecRegistry {

    private final Map<String, ComponentCodec> codecs = new LinkedHashMap<>();

    public void register(ComponentCodec codec) {
        codecs.put(codec.getIdentifier(), codec);
    }

    public Collection<ComponentCodec> getCodecs() {
        return Collections.unmodifiableCollection(codecs.values());
    }

}

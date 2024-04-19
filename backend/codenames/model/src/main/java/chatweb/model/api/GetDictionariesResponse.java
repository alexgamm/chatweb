package chatweb.model.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GetDictionariesResponse {
    private final List<DictionaryDto> dictionaries;
}

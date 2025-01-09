package org.example.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.example.dto.AdsDto;
import org.example.model.Ads;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-08T15:28:51+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AdsMapperImpl implements AdsMapper {

    @Override
    public AdsDto toDto(Ads ads) {
        if ( ads == null ) {
            return null;
        }

        AdsDto adsDto = new AdsDto();

        adsDto.setTitle( ads.getTitle() );
        adsDto.setDescription( ads.getDescription() );
        adsDto.setPrice( ads.getPrice() );

        return adsDto;
    }

    @Override
    public List<AdsDto> toDtoList(List<Ads> adsList) {
        if ( adsList == null ) {
            return null;
        }

        List<AdsDto> list = new ArrayList<AdsDto>( adsList.size() );
        for ( Ads ads : adsList ) {
            list.add( toDto( ads ) );
        }

        return list;
    }

    @Override
    public Ads toEntity(AdsDto adsDto) {
        if ( adsDto == null ) {
            return null;
        }

        Ads ads = new Ads();

        ads.setTitle( adsDto.getTitle() );
        ads.setDescription( adsDto.getDescription() );
        if ( adsDto.getPrice() != null ) {
            ads.setPrice( adsDto.getPrice() );
        }

        return ads;
    }

    @Override
    public List<Ads> toEntityList(List<AdsDto> adsDtoList) {
        if ( adsDtoList == null ) {
            return null;
        }

        List<Ads> list = new ArrayList<Ads>( adsDtoList.size() );
        for ( AdsDto adsDto : adsDtoList ) {
            list.add( toEntity( adsDto ) );
        }

        return list;
    }
}

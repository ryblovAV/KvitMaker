/* Сценарий для квитанций.
Файл сценария обязан содержать:
1. Процедуру формирования квтианций для МКД.
2. Процедуру формирования квтианций для неМКД.
3. Запрос на получение списка квитанций МКД.
4. Запрос на получение списка квитанций неМКД.
5. Запрос на получение наименования Организации.

Не используйте точку с запятой в комментариях, ибо по ней разделяются запросы в java-коде.
Не меняйте имена переменных, ибо по ним биндятся параметры.
И соблюдайте перечисленный порядок запросов, блеать!
*/


-- Процедура формирования квтианций для МКД.
-- Дата (:pdat) обязательна (первое число месяца).
-- Остальные параметры можно задавать в null, но не все сразу:
-- обязательно должны быть указаны либо ПАУ и район, либо ID МКД.
call lcmccb.p_kwee.pcm_kvee_mkd
              (
              vpdat => :pdat
              , pleskgesk => :pleskgesk
              , pbd_lesk => :pbd_lesk
              , pprnt_prem_id => :mkd_id
              );

-- Процедура формирования квтианций для неМКД.
call lcmccb.p_kwee.pcm_kvee_notmkd(
                     vpdat => :pdat
                     , pleskgesk => :pleskgesk
                     , pbd_lesk => :pbd_lesk
                     );

-- Запрос на получение списка квитанций МКД.
-- Первые три поля (bd_lesk, postal, addressshort) используются для сортировки и разделения
-- и в выгрузке присутствовать не должны.
select bd_lesk,
       postal,
       upper(addressshort) addressshort,
       DBM_NAME,
       '' " ",
       Company,
       Inn,
       AccountNum,
       BankName,
       AccountNumOff,
       Bik,
       Month,
       Period,
       Date1,
       Date2,
       Phone,
       Email,
       OrgAddress,
       Barcode,
       Ls,
       Fio,
       Address,
       Count,
       CountRes,
       Dpokaz,
       Npokaz,
       Srasch,
       FSumType,
       Dolg,
       Oplata,
       OldPokaz1,
       OldPokaz2,
       OldPokaz3,
       OldPokaz4,
       OldPokaz5,
       Pokaz1,
       Pokaz2,
       Pokaz3,
       Pokaz4,
       Pokaz5,
       KoefTr1,
       KoefTr2,
       KoefTr3,
       KoefTr4,
       KoefTr5,
       Kwt1,
       Kwt2,
       Kwt3,
       Kwt4,
       Kwt5,
       Tarif1,
       Tarif2,
       Tarif3,
       Tarif4,
       Tarif5,
       Sum1,
       Sum2,
       Sum3,
       Sum4,
       Sum5,
       Sum,
       to_char(round(lcmccb.fcm_to_number(RKwt1), 2)) RKwt1,
       to_char(round(lcmccb.fcm_to_number(RKwt2), 2)) RKwt2,
       to_char(round(lcmccb.fcm_to_number(RKwt3), 2)) RKwt3,
       to_char(round(lcmccb.fcm_to_number(RKwt4), 2)) RKwt4,
       to_char(round(lcmccb.fcm_to_number(RKwt5), 2)) RKwt5,
       to_char(round(lcmccb.fcm_to_number(RKwt), 2)) RKwt,
       RSum1,
       RSum2,
       RSum3,
       RSum4,
       RSum5,
       Rsum,
       Total1,
       Total2,
       Total3,
       Total4,
       Total5,
       Total,
       DebtIn,
       TotalPay,
       PrePay,
       OdnTitle,
       Odn1,
       Odn2,
       Odn3,
       Odn4,
       Odn5,
       Odn6,
       Odn7,
       Odn8,
       DebtInTitle,
       DebtIn1,
       DebtIn2,
       DebtIn3,
       DebtIn4,
       DebtIn5,
       DebtLaw1,
       DebtLaw2,
       DebtLaw3,
       DebtLaw,
       Service1,
       Service2,
       Service3,
       Service,
       SOdpu1,
       SOdpu2,
       SOdpu3,
       SOdpu4,
       SOdpu5,
       SOdpu6,
       SOdpu7,
       SOdpu8,
       SOdpu9,
       SOdpu10,
       EOdpu1,
       EOdpu2,
       EOdpu3,
       EOdpu4,
       EOdpu5,
       EOdpu6,
       EOdpu7,
       EOdpu8,
       EOdpu9,
       EOdpu10,
       Diff1,
       Diff2,
       Diff3,
       Diff4,
       Diff5,
       Diff6,
       Diff7,
       Diff8,
       Diff9,
       Diff10,
       Ratio1,
       Ratio2,
       Ratio3,
       Ratio4,
       Ratio5,
       Ratio6,
       Ratio7,
       Ratio8,
       Ratio9,
       Ratio10,
       TotalKwt1,
       TotalKwt2,
       TotalKwt3,
       TotalKwt4,
       TotalKwt5,
       TotalKwt6,
       TotalKwt7,
       TotalKwt8,
       TotalKwt9,
       TotalKwt10,
       TotalKwt,
       CTarifTitle,
       CTarifDate1,
       CTarifDate2,
       VTarifDate1,
       VTarifDate2,
       CTarif1,
       CTarif2,
       VTarif1,
       VTarif2,
       DCTarif1,
       DCTarif2,
       NCTarif1,
       NCTarif2,
       DVTarif1,
       DVTarif2,
       NVTarif1,
       NVTarif2,
       FInfo1,
       Info1,
       Info2,
       rownum KolKv,
       PayType,
       Npuch,
       KolKom,
       TINF,
       DATEPOKAZ,
       OZPU,
       CDAY,
       CNIGHT,
       DOLZHNIK,
       SOdpu11,
       SOdpu12,
       SOdpu13,
       SOdpu14,
       EOdpu11,
       EOdpu12,
       EOdpu13,
       EOdpu14,
       Diff11,
       Diff12,
       Diff13,
       Diff14,
       Ratio11,
       Ratio12,
       Ratio13,
       Ratio14,
       TotalKwt11,
       TotalKwt12,
       TotalKwt13,
       TotalKwt14,
       KVITGP,
       KVITDELE,
       KVITOGRA,
       KVITGPDELEOGRA
  from (select distinct
               bd_lesk,
               leskgesk,
               upper(addressshort) addressshort,
               DBM_NAME,
               Company,
               Inn,
               AccountNum,
               BankName,
               AccountNumOff,
               Bik,
               Month,
               Period,
               Date1,
               Date2,
               Phone,
               Email,
               OrgAddress,
               Barcode,
               Ls,
               Fio,
               Address,
               Count,
               CountRes,
               Dpokaz,
               Npokaz,
               Srasch,
               FSumType,
               Dolg,
               Oplata,
               OldPokaz1,
               OldPokaz2,
               OldPokaz3,
               OldPokaz4,
               OldPokaz5,
               Pokaz1,
               Pokaz2,
               Pokaz3,
               Pokaz4,
               Pokaz5,
               KoefTr1,
               KoefTr2,
               KoefTr3,
               KoefTr4,
               KoefTr5,
               Kwt1,
               Kwt2,
               Kwt3,
               Kwt4,
               Kwt5,
               Tarif1,
               Tarif2,
               Tarif3,
               Tarif4,
               Tarif5,
               Sum1,
               Sum2,
               Sum3,
               Sum4,
               Sum5,
               Sum,
               RKwt1,
               RKwt2,
               RKwt3,
               RKwt4,
               RKwt5,
               RKwt,
               RSum1,
               RSum2,
               RSum3,
               RSum4,
               RSum5,
               Rsum,
               Total1,
               Total2,
               Total3,
               Total4,
               Total5,
               Total,
               DebtIn,
               TotalPay,
               PrePay,
               OdnTitle,
               Odn1,
               Odn2,
               Odn3,
               Odn4,
               Odn5,
               Odn6,
               Odn7,
               Odn8,
               DebtInTitle,
               DebtIn1,
               DebtIn2,
               DebtIn3,
               DebtIn4,
               DebtIn5,
               DebtLaw1,
               DebtLaw2,
               DebtLaw3,
               DebtLaw,
               Service1,
               Service2,
               Service3,
               Service,
               SOdpu1,
               SOdpu2,
               SOdpu3,
               SOdpu4,
               SOdpu5,
               SOdpu6,
               SOdpu7,
               SOdpu8,
               SOdpu9,
               SOdpu10,
               EOdpu1,
               EOdpu2,
               EOdpu3,
               EOdpu4,
               EOdpu5,
               EOdpu6,
               EOdpu7,
               EOdpu8,
               EOdpu9,
               EOdpu10,
               Diff1,
               Diff2,
               Diff3,
               Diff4,
               Diff5,
               Diff6,
               Diff7,
               Diff8,
               Diff9,
               Diff10,
               Ratio1,
               Ratio2,
               Ratio3,
               Ratio4,
               Ratio5,
               Ratio6,
               Ratio7,
               Ratio8,
               Ratio9,
               Ratio10,
               TotalKwt1,
               TotalKwt2,
               TotalKwt3,
               TotalKwt4,
               TotalKwt5,
               TotalKwt6,
               TotalKwt7,
               TotalKwt8,
               TotalKwt9,
               TotalKwt10,
               TotalKwt,
               CTarifTitle,
               CTarifDate1,
               CTarifDate2,
               VTarifDate1,
               VTarifDate2,
               CTarif1,
               CTarif2,
               VTarif1,
               VTarif2,
               DCTarif1,
               DCTarif2,
               NCTarif1,
               NCTarif2,
               DVTarif1,
               DVTarif2,
               NVTarif1,
               NVTarif2,
               FInfo1,
               Info1,
               Info2,
               PayType,
               Npuch,
               KolKom,
               TINF,
               DATEPOKAZ,
               OZPU,
               CDAY,
               CNIGHT,
               DOLZHNIK,
               SOdpu11,
               SOdpu12,
               SOdpu13,
               SOdpu14,
               EOdpu11,
               EOdpu12,
               EOdpu13,
               EOdpu14,
               Diff11,
               Diff12,
               Diff13,
               Diff14,
               Ratio11,
               Ratio12,
               Ratio13,
               Ratio14,
               TotalKwt11,
               TotalKwt12,
               TotalKwt13,
               TotalKwt14,
               KVITGP,
               KVITDELE,
               KVITOGRA,
               KVITGPDELEOGRA,
               address2,
               address3,
               address4,
               postal
          from lcmccb.CM_KVEE_MKD_CSV k
         where pdat = :pdat
           and (:mkd_id is null
                and leskgesk = :pleskgesk
                and bd_lesk = :pbd_lesk
                or bill_id in (select bs.bill_id
                                 from rusadm.ci_bseg bs
                                where trunc(bs.end_dt, 'mm') = :pdat
                                  and bs.bseg_stat_flg = 50
                                  and exists (select null
                                                from rusadm.ci_prem  pr
                                               where pr.prem_id = bs.prem_id
                                                 and pr.prnt_prem_id = :mkd_id)))
         order by bd_lesk,
                  postal,
                  upper(addressshort),
                  upper(address3),
                  to_number(regexp_replace(address2,'[^[[:digit:]]]*')),
                  upper(address2),
                  to_number(regexp_replace(address4,'[^[[:digit:]]]*')),
                  upper(address4));

-- Запрос на получение списка квитанций неМКД.
-- Первые три поля (bd_lesk, postal, addressshort) используются для сортировки и разделения
-- и в выгрузке присутствовать не должны.
select bd_lesk,
       postal,
       upper(addressshort) addressshort,
       DBM_NAME,
       '' " ",
       Company,
       Inn,
       AccountNum,
       BankName,
       AccountNumOff,
       Bik,
       Month,
       Period,
       Date1,
       Date2,
       Phone,
       Email,
       OrgAddress,
       Barcode,
       Ls,
       Fio,
       Address,
       Count,
       Dpokaz,
       Npokaz,
       Srasch,
       FSumType,
       Dolg,
       Oplata,
       OldPokaz1,
       OldPokaz2,
       OldPokaz3,
       OldPokaz4,
       Pokaz1,
       Pokaz2,
       Pokaz3,
       Pokaz4,
       KoefTr1,
       KoefTr2,
       KoefTr3,
       KoefTr4,
       Kwt1,
       Kwt2,
       Kwt3,
       Kwt4,
       Tarif1,
       Tarif2,
       Tarif3,
       Tarif4,
       Sum1,
       Sum2,
       Sum3,
       Sum4,
       Sum,
       to_char(round(lcmccb.fcm_to_number(RKwt1), 2)) RKwt1,
       to_char(round(lcmccb.fcm_to_number(RKwt2), 2)) RKwt2,
       to_char(round(lcmccb.fcm_to_number(RKwt3), 2)) RKwt3,
       to_char(round(lcmccb.fcm_to_number(RKwt4), 2)) RKwt4,
       to_char(round(lcmccb.fcm_to_number(RKwt), 2)) RKwt,
       RSum1,
       RSum2,
       RSum3,
       RSum4,
       Rsum,
       Total1,
       Total2,
       Total3,
       Total4,
       Total,
       DebtInTitle,
       DebtIn,
       TotalPay,
       PrePay,
       DebtIn1,
       DebtIn2,
       DebtIn3,
       DebtIn4,
       DebtIn5,
       DebtLaw1,
       DebtLaw2,
       DebtLaw3,
       DebtLaw,
       Service1,
       Service2,
       Service3,
       Service,
       CTarifTitle,
       CTarifDate1,
       CTarifDate2,
       VTarifDate1,
       VTarifDate2,
       CTarif1,
       CTarif2,
       VTarif1,
       VTarif2,
       DCTarif1,
       DCTarif2,
       NCTarif1,
       NCTarif2,
       DVTarif1,
       DVTarif2,
       NVTarif1,
       NVTarif2,
       FInfo1,
       Info1,
       Info2,
       FInfo3,
       Info3,
       rownum KolKv,
       PayType,
       Npuch,
       KolKom,
       TINF,
       DATEPOKAZ,
       OZPU,
       CDAY,
       CNIGHT,
       DOLZHNIK,
       KVITGP,
       KVITDELE,
       KVITOGRA,
       KVITGPDELEOGRA
  from (select distinct
               bd_lesk,
               leskgesk,
               addressshort,
               DBM_NAME,
               Company,
               Inn,
               AccountNum,
               BankName,
               AccountNumOff,
               Bik,
               Month,
               Period,
               Date1,
               Date2,
               Phone,
               Email,
               OrgAddress,
               Barcode,
               Ls,
               Fio,
               Address,
               Count,
               Dpokaz,
               Npokaz,
               Srasch,
               FSumType,
               Dolg,
               Oplata,
               OldPokaz1,
               OldPokaz2,
               OldPokaz3,
               OldPokaz4,
               Pokaz1,
               Pokaz2,
               Pokaz3,
               Pokaz4,
               KoefTr1,
               KoefTr2,
               KoefTr3,
               KoefTr4,
               Kwt1,
               Kwt2,
               Kwt3,
               Kwt4,
               Tarif1,
               Tarif2,
               Tarif3,
               Tarif4,
               Sum1,
               Sum2,
               Sum3,
               Sum4,
               Sum,
               RKwt1,
               RKwt2,
               RKwt3,
               RKwt4,
               RKwt,
               RSum1,
               RSum2,
               RSum3,
               RSum4,
               Rsum,
               Total1,
               Total2,
               Total3,
               Total4,
               Total,
               DebtInTitle,
               DebtIn,
               TotalPay,
               PrePay,
               DebtIn1,
               DebtIn2,
               DebtIn3,
               DebtIn4,
               DebtIn5,
               DebtLaw1,
               DebtLaw2,
               DebtLaw3,
               DebtLaw,
               Service1,
               Service2,
               Service3,
               Service,
               CTarifTitle,
               CTarifDate1,
               CTarifDate2,
               VTarifDate1,
               VTarifDate2,
               CTarif1,
               CTarif2,
               VTarif1,
               VTarif2,
               DCTarif1,
               DCTarif2,
               NCTarif1,
               NCTarif2,
               DVTarif1,
               DVTarif2,
               NVTarif1,
               NVTarif2,
               FInfo1,
               Info1,
               Info2,
               FInfo3,
               Info3,
               PayType,
               Npuch,
               KolKom,
               TINF,
               DATEPOKAZ,
               OZPU,
               CDAY,
               CNIGHT,
               DOLZHNIK,
               KVITGP,
               KVITDELE,
               KVITOGRA,
               KVITGPDELEOGRA,
               address2,
               address3,
               address4,
               postal
          from lcmccb.CM_KVEE_NOTMKD_CSV
         where pdat = :pdat
           and leskgesk = :pleskgesk
           and bd_lesk = :pbd_lesk
         order by bd_lesk,
                  postal,
                  upper(addressshort),
                  upper(address3),
                  to_number(regexp_replace(address2,'[^[[:digit:]]]*')),
                  upper(address2),
                  to_number(regexp_replace(address4,'[^[[:digit:]]]*')),
                  upper(address4));

-- Запрос на получение наименования Организации.
select extractValue(XMLType(lv.BO_DATA_AREA), 'tarrifTable/company') company
  from rusadm.F1_EXT_LOOKUP_VAL lv
 where trim(lv.bus_obj_cd) = case
                               when to_number(:pbd_lesk) between 1 and 18 then
                                 'CM_EL_ORG'
                               else
                                 'CM_EL_ORG_G'
                             end
   and nvl(extractValue(XMLType(lv.BO_DATA_AREA), 'tarrifTable/kod_bd'), :pbd_lesk)  = :pbd_lesk
   and rownum = 1;
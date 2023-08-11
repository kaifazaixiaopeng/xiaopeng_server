//package com.xiaopeng.server.vx.controller;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.excel.EasyExcel;
//import com.baiinfo.backend.common.util.ContextUtil;
//import com.baiinfo.backend.common.util.MinioUtil;
//import com.baiinfo.backend.config.executor.ExecutorUtil;
//import com.baiinfo.backend.module.customdata.converts.LocalDateConverter;
//import com.baiinfo.backend.module.customdata.entity.Menu;
//import com.baiinfo.backend.module.customdata.service.MenuService;
//import com.baiinfo.backend.module.ironsteel.utils.IronsteelStringConverter;
//import com.baiinfo.backend.module.news.vo.SimpleWriteData;
//import com.baiinfo.backend.module.pricetarget.entity.TargetPrice;
//import com.baiinfo.backend.module.pricetarget.service.TargetPriceService;
//import com.baiinfo.backend.module.product.entity.ProductEntity;
//import com.baiinfo.backend.module.product.service.ProductService;
//import com.baiinfo.backend.module.product.vo.ProductVo;
//import com.baiinfo.backend.module.standard.entity.*;
//import com.baiinfo.backend.module.standard.service.*;
//import com.baiinfo.backend.module.supplydemand.config.ImportConfiguration;
//import com.baiinfo.backend.module.supplydemand.entity.BaseImport;
//import com.baiinfo.backend.module.supplydemand.enums.ErrorCode;
//import com.baiinfo.backend.module.supplydemand.enums.VaildTypeEnum;
//import com.baiinfo.backend.module.supplydemand.exception.ImportVaildException;
//import com.baiinfo.backend.module.supplydemand.listener.TemplateImportListener;
//import com.baiinfo.backend.module.supplydemand.service.FeProductlinkprocessService;
//import com.baiinfo.backend.module.supplydemand.until.ImportErrExcelUtils;
//import com.baiinfo.backend.module.supplydemand.until.ObjUtil;
//import com.baiinfo.backend.util.CacheUtil;
//import com.baiinfo.core.tools.CollectionUtil;
//import com.baiinfo.core.tools.R;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.ObjectUtils;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ForkJoinPool;
//import java.util.stream.Collectors;
//
//import static com.baiinfo.backend.module.supplydemand.enums.VaildTypeEnum.*;
//
//@Slf4j
//public abstract class BaseImportController<T extends BaseImport> {
//
//    protected static final Validator validator;
//
//    public static final int THREAD_POOLSIZE = (Runtime.getRuntime().availableProcessors()) < 16 ? 16 : Runtime.getRuntime().availableProcessors();
//
//    static {
//        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
//        validator = vf.getValidator();
//    }
//
//    private final static String IMPORT = "import";
//    private final static String SPIT = "/";
//    @Resource
//    protected ICyCorpService iCyCorpService;
//    @Autowired
//    protected ProductService productService;
//    @Resource
//    IUnitInfoService unitInfoService;
//    @Resource
//    private ITbMarketService iTbMarketService;
//    @Resource
//    private MinioUtil minioUtil;
//    @Resource
//    protected IRegionService regionService;
//    @Resource
//    private ITbRegionService iTbRegionService;
//    @Autowired
//    private ITbCountryService countryService;
//    @Autowired
//    private MenuService menuService;
//    @Autowired
//    protected ImportConfiguration importConfiguration;
//    @Autowired
//    private FeProductlinkprocessService feProductlinkprocessService;
//
//    @Autowired
//    private TargetPriceService targetPriceService;
//
//    @Autowired
//    private IAreaService iAreaService;
//    @Autowired
//    private CacheUtil cacheUtil;
//
//    protected List<T> parseExcel(InputStream inputStream, List<T> errList) {
//        return EasyExcel.read(inputStream, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0],
//                        new TemplateImportListener(errList)).registerConverter(new IronsteelStringConverter())
//                .registerConverter(new LocalDateConverter())
//                // 设置sheet,默认读取第一个
//                .sheet(0)
//                // 设置标题所在行数
//                .headRowNumber(1).doReadSync();
//    }
//
//    protected boolean vaildProductId(Integer productId) {
//        if (ObjectUtil.isEmpty(productId)) return true;
//        ProductVo productById = productService.getProductById(productId, null);
//        if (ObjectUtil.isEmpty(productById)) return false;
//        return true;
//    }
//
//    protected Long vaildUnit(String unitName, String parentName) {
//        if (StrUtil.isEmpty(unitName)) return 0L;
//        List<UnitInfo> unitInfoByName = unitInfoService.getUnitByName(unitName);
//        if (CollectionUtil.isEmpty(unitInfoByName)) return 0L;
//        else if (unitInfoByName.size() > 1) return 0L;
//        else return unitInfoByName.get(0).getUnitId();
//
//    }
//
//    protected boolean vaildMarketId(Integer marketId) {
//        if (ObjectUtil.isEmpty(marketId)) return true;
//        TbMarket tbMarket = iTbMarketService.getById(marketId);
//        if (ObjectUtil.isEmpty(tbMarket)) {
//            return false;
//        }
//        return true;
//    }
//
//    protected boolean vaildProcessId(Integer productId, Integer processId) {
////        if (ObjectUtil.isEmpty(processId)) return true;
////        List<FeProductProcessVO> processByproductId = feProductlinkprocessService.getProcessByproductId(productId, processId);
////        if (CollectionUtil.isEmpty(processByproductId)) return false;
//        return true;
//    }
//
//    protected boolean vaildCorpId(Integer corpId) {
//        if (ObjectUtil.isEmpty(corpId)) return true;
//        CyCorp byId = iCyCorpService.getById(corpId);
//        if (ObjectUtil.isEmpty(byId)) return false;
//        return true;
//    }
//
//    protected boolean vaildProvince(Integer provinceId) {
//        if (ObjectUtil.isEmpty(provinceId)) return true;
//        QueryWrapper<Region> qw = new QueryWrapper();
//        qw.lambda().eq(Region::getLevel, 1).eq(Region::getIsActive, 1).eq(Region::getAdCode, String.valueOf(provinceId));
//        Region region = regionService.getOne(qw);
//        if (ObjectUtil.isEmpty(region)) return false;
//        return true;
//    }
//
//    protected boolean vaildProvince(Integer provinceId, Integer level) {
//        if (ObjectUtil.isEmpty(provinceId)) return true;
//        QueryWrapper<Region> qw = new QueryWrapper();
//        qw.lambda().eq(Region::getLevel, level).eq(Region::getIsActive, 1).eq(Region::getAdCode, String.valueOf(provinceId));
//        Region region = regionService.getOne(qw);
//        if (ObjectUtil.isEmpty(region)) return false;
//        return true;
//    }
//
//    protected boolean vaildProvince(String provinceName) {
//        if (StrUtil.isEmpty(provinceName)) return true;
//        QueryWrapper<TbRegion> qw = new QueryWrapper();
//        qw.lambda().eq(TbRegion::getIsShow, 1).eq(TbRegion::getRegionLevel, 1).eq(TbRegion::getRegionName, provinceName);
//        TbRegion region = iTbRegionService.getOne(qw);
//        if (ObjectUtil.isEmpty(region)) return false;
//        return true;
//    }
//
//    protected boolean vaildArea(Integer areaId) {
//        if (ObjectUtil.isEmpty(areaId)) return true;
//        TbCountry country = countryService.getById(areaId);
//        if (ObjectUtils.isEmpty(country)) {
//            return false;
//        }
//        return true;
//    }
//
//    protected boolean vaildMenu(Integer menuId) {
//        if (ObjectUtil.isEmpty(menuId)) return true;
//        Menu byId = menuService.getById(menuId);
//        if (ObjectUtil.isEmpty(byId)) return false;
//        return true;
//    }
//
//    protected boolean vaildTargetId(Long targetId) {
//        if (ObjectUtil.isEmpty(targetId)) return true;
//        TargetPrice targetPrice = targetPriceService.getById(targetId);
//        if (ObjectUtil.isEmpty(targetPrice)) return false;
//        return true;
//    }
//
//    protected boolean vaildArea(String areaName) {
//        if (StrUtil.isEmpty(areaName)) return true;
//        QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
//        areaQueryWrapper.lambda().eq(Area::getAreaName, areaName).eq(Area::getParentId, 0);
//        List<Area> list = iAreaService.list(areaQueryWrapper);
//        if (CollectionUtil.isEmpty(list)) return false;
//        return true;
//    }
//
//    protected void checkValidList(List<T> vaildList, List<T> errList) {
//
//    }
//
//    protected void checkValidListLogName(List<T> vaildList, List<T> errList, Long userId) {
//
//    }
//
//    protected List<T> filterVaildList(List<T> vaildList) {
//        return vaildList.parallelStream().filter(i -> i.isCheckPass()).collect(Collectors.toList());
//    }
//
//    protected R saveOrUpdate(T t, Integer isBatch) {
//        return saveOrUpdate(t);
//    }
//
//    ;
//
//    @Deprecated
//    protected R saveOrUpdate(T t) {
//        return saveOrUpdate(t, 1);
//    }
//
//    ;
//
//    protected R saveOrUpdate(T t, Long logName) {
//        return saveOrUpdate(t, logName);
//    }
//
//    protected void transParam(List<T> transList, List<T> errList, boolean trans) {
//
//    }
//
//    protected void validFileSizeAndFormat(MultipartFile file) {
//    }
//
//    protected void transProductId(T t, Integer productId) throws IllegalAccessException {
//        Field productIdField = ObjUtil.getField(t.getClass(), true, PRODUCT.getFieldName(), "productid", "productID");
//        if (productIdField == null) return;
//        productIdField.set(t, productId);
//    }
//
//    @PostMapping("/import")
//    protected R importExcel(@RequestParam("file") MultipartFile file) {
//        List<T> errList = new ArrayList<>();
//        List<T> tList = null;
//        try {
//            validFileSizeAndFormat(file);
//            tList = parseExcel(file.getInputStream(), errList);
//            transParam(filterVaildList(tList), errList, false);
//            checkValidList(filterVaildList(tList), errList);
//            if (CollectionUtil.isNotEmpty(errList)) {
//                throw new ImportVaildException(VaildTypeEnum.CHECK, "");
//            }
//            transParam(tList, errList, true);
//            async(group(tList), errList);
//        } catch (ImportVaildException e) {
//            if (PARSE.getFieldName().equals(e.getCode().getFieldName())) {
//                return R.fail(ErrorCode.PARSE_FAIL.getCode(), e.getMessage());
//            } else if (CHECK.getFieldName().equals(e.getCode().getFieldName())) {
//                return uploadFile(file.getOriginalFilename(), tList);
//            }
//        } catch (Exception e) {
//            if (e.getCause() instanceof ImportVaildException) {
//                return R.fail(ErrorCode.PARSE_FAIL.getCode(), e.getCause().getMessage());
//            }
//            return R.fail(e.getMessage());
//        }
//        return R.data(ErrorCode.SUCCESS);
//    }
//
//    //限制上传条数的导入接口
//    @PostMapping("/importLimitation")
//    protected R importLimitation(@RequestParam("file") MultipartFile file) {
//        List<T> errList = new ArrayList<>();
//        List<T> tList = null;
//        try {
//            tList = parseExcel(file.getInputStream(), errList);
//            if (tList.size() > 5000) {
//                return R.fail("上传条数不能超过5000条");
//            }
//            transParam(filterVaildList(tList), errList, false);
//            checkValidList(filterVaildList(tList), errList);
//            if (CollectionUtil.isNotEmpty(errList)) {
//                throw new ImportVaildException(VaildTypeEnum.CHECK, "");
//            }
//            transParam(tList, errList, true);
//            async(group(tList), errList);
//        } catch (ImportVaildException e) {
//            if (PARSE.getFieldName().equals(e.getCode().getFieldName())) {
//                return R.fail(ErrorCode.PARSE_FAIL.getCode(), e.getMessage());
//            } else if (CHECK.getFieldName().equals(e.getCode().getFieldName())) {
//                return uploadFile(file.getOriginalFilename(), tList);
//            }
//        } catch (Exception e) {
//            return R.fail(e.getMessage());
//        }
//        return R.data(ErrorCode.SUCCESS);
//    }
//
//    @PostMapping("/import1")
//    protected R impor1tExcel(@RequestParam("file") MultipartFile file) {
//        Long userId = ContextUtil.getUserId();
//        List<T> errList = new ArrayList<>();
//        List<T> tList = null;
//        Boolean isBlock = true;
//        Boolean isAdmin = true;
//        try {
//            tList = parseExcel(file.getInputStream(), errList);
//            checkValidListLogName(filterVaildList(tList), errList, userId);
//            if (CollectionUtil.isNotEmpty(errList)) {
//                throw new ImportVaildException(VaildTypeEnum.CHECK, "");
//            }
//            async1(group1(tList), errList, userId);
//            isBlock = checkIsBlock(tList);
//            //判断是否有上级
//            isAdmin = checkAdmin(userId);
//        } catch (ImportVaildException e) {
//            if (PARSE.getFieldName().equals(e.getCode().getFieldName())) {
//                return R.fail(ErrorCode.PARSE_FAIL.getCode(), e.getMessage());
//            } else if (CHECK.getFieldName().equals(e.getCode().getFieldName())) {
//                return uploadFile(file.getOriginalFilename(), tList);
//            }
//        } catch (Exception e) {
//            if (e.getCause() instanceof ImportVaildException) {
//                return R.fail(ErrorCode.PARSE_FAIL.getCode(), e.getCause().getMessage());
//            }
//            return R.fail(e.getMessage());
//        }
//        if (!isBlock && isAdmin) {
//            return R.data(200, "导入成功", "导入数据中部分价格需要审批，请及时联系审批人审批");
//        }
//        if (!isBlock && !isAdmin) {
//            return R.data(200, "导入成功", "数据需要审批但未找到上级人员，请及时设置上级");
//        }
//        return R.data(ErrorCode.SUCCESS);
//    }
//
//    private R uploadFile(String fileName, List<T> tList) {
//        //生成文件并上传服务器
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ByteArrayInputStream byteArrayInputStream = null;
//        try {
//            SimpleWriteData simpleWriteData = new SimpleWriteData(fileName, tList);
//            ImportErrExcelUtils.simpleWrite(simpleWriteData, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0], outputStream);
//            byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            String objName = new StringBuilder().append(IMPORT).append(SPIT).append(DateUtil.today()).append(SPIT).append(fileName).toString();
//            minioUtil.uploadFile(byteArrayInputStream, objName);
//            return R.fail(ErrorCode.IMPORT_FAIL.getCode(), objName);
//        } catch (Exception e) {
//            log.error("导入失败", e);
//            return R.fail("上传异常数据至服务器失败");
//        } finally {
//            IOUtils.closeQuietly(outputStream);
//            IOUtils.closeQuietly(byteArrayInputStream);
//        }
//    }
//
//    protected boolean afterSave(List<T> tList) {
//        return true;
//    }
//
//    protected boolean beforeSave(List<T> tList) {
//        return true;
//    }
//    /*
//     *  i 默认从1开始
//     * */
//
//    private void async(List<ImportDataGroup> group, final List<T> errList) throws InterruptedException {
//        CountDownLatch countDownLatch = new CountDownLatch(group.size());
//        ExecutorService localExecutor = ExecutorUtil.createLocalExecutor(group.size() > 10 ? 10 : group.size(), this.getClass().getCanonicalName());
//        group.parallelStream().forEach(i -> {
//            localExecutor.submit(new Runnable() {
//                @Override
//                public void run() {
//                    log.info(Thread.currentThread().getName() + " 分组id：" + i.getKey());
//                    if (!beforeSave(i.tList)) {
//                        errList.addAll(i.tList);
//                        countDownLatch.countDown();
//                        return;
//                    }
//                    i.tList.stream().forEach(t -> {
//                        try {
//                            R r = saveOrUpdate(t, 1);
//                            if (!r.isSuccess()) {
//                                ObjUtil.setVal(t, r.getMsg());
//                                errList.add(t);
//                            }
//                        } catch (MybatisPlusException e) {
//                            if (e.getMessage().equals("One record is expected, but the query result is multiple records")) {
//                                ObjUtil.setVal(t, "表中出现重复数据");
//                            } else {
//                                log.error(e.getMessage(), e);
//                                ObjUtil.setVal(t, "导入失败");
//                            }
//                            errList.add(t);
//                        } catch (Exception e) {
//                            log.error(e.getMessage(), e);
//                            ObjUtil.setVal(t, "导入失败");
//                            errList.add(t);
//                        }
//                    });
//                    if (CollectionUtil.isEmpty(errList) && !afterSave(i.tList)) {
//                        i.tList.parallelStream().forEach(i -> ObjUtil.setVal(i, "新增或者衍生数据异常"));
//                        errList.addAll(i.tList);
//                    }
//                    countDownLatch.countDown();
//                }
//            });
//        });
//        countDownLatch.await();
//        localExecutor.shutdown();
//        if (CollectionUtil.isNotEmpty(errList)) {
//            throw new ImportVaildException(VaildTypeEnum.CHECK, "");
//        }
//    }
//
//    private boolean setErrMsg(String errMsg, T i, VaildTypeEnum vaildTypeEnum, Field field) {
//        try {
//            if (field == null) return false;
//            Object o = field.get(i);
//            if (o != null && o.getClass() == Date.class) {
//                ObjUtil.setVal(i, vaildTypeEnum.getMsg() + errMsg);
//                return true;
//            }
//            if (errMsg.equals(String.valueOf(o))) {
//                ObjUtil.setVal(i, vaildTypeEnum.getMsg() + errMsg);
//                return true;
//            }
//        } catch (IllegalAccessException ex) {
//            throw new RuntimeException(ex);
//        }
//        return false;
//    }
//
//    protected void handleVaildErr(ImportVaildException e, List<T> tList, List<T> errList) {
//        tList.parallelStream().forEach(i -> {
//            Field field = null;
//            VaildTypeEnum vaildTypeEnum = null;
//            switch (e.getCode()) {
//                case PRODUCT:
//                    field = ObjUtil.getField(i.getClass(), true, PRODUCT.getFieldName(), "productid", "productID");
//                    vaildTypeEnum = PRODUCT;
//                    break;
//                case PRODUCT_NO_PERSSION:
//                    field = ObjUtil.getField(i.getClass(), true, PRODUCT_NO_PERSSION.getFieldName(), "productid", "productID");
//                    vaildTypeEnum = PRODUCT_NO_PERSSION;
//                    break;
//                case PRODUCT_OVER:
//                    field = ObjUtil.getField(i.getClass(), true, PRODUCT_OVER.getFieldName(), "productid", "productID");
//                    vaildTypeEnum = PRODUCT_OVER;
//                    break;
//                case UNIT:
//                    field = ObjUtil.getField(i.getClass(), true, UNIT.getFieldName());
//                    vaildTypeEnum = UNIT;
//                    break;
//                case CURR_UNIT:
//                    field = ObjUtil.getField(i.getClass(), true, CURR_UNIT.getFieldName());
//                    vaildTypeEnum = CURR_UNIT;
//                    break;
//                case MARKET:
//                    field = FieldUtils.getField(i.getClass(), MARKET.getFieldName(), true);
//                    vaildTypeEnum = MARKET;
//                    break;
//                case PROCESS:
//                    field = ObjUtil.getField(i.getClass(), true, PROCESS.getFieldName(), "processID");
//                    vaildTypeEnum = PROCESS;
//                    break;
//                case CORP:
//                    field = ObjUtil.getField(i.getClass(), true, CORP.getFieldName(), "corpID", "corpid", "corpId");
//                    vaildTypeEnum = CORP;
//                    break;
//                case AREA:
//                    field = FieldUtils.getField(i.getClass(), AREA.getFieldName(), true);
//                    vaildTypeEnum = AREA;
//                    break;
//                case PROVINCE:
//                    field = ObjUtil.getField(i.getClass(), true, PROVINCE.getFieldName(), "proviceId", "proviceID", "areaId");
//                    vaildTypeEnum = PROVINCE;
//                    break;
//                case CITY:
//                    field = FieldUtils.getField(i.getClass(), CITY.getFieldName(), true);
//                    vaildTypeEnum = CITY;
//                    break;
//                case COUNTY:
//                    field = FieldUtils.getField(i.getClass(), COUNTY.getFieldName(), true);
//                    vaildTypeEnum = COUNTY;
//                    break;
//                case PROVINCE_NAME:
//                    field = ObjUtil.getField(i.getClass(), true, PROVINCE_NAME.getFieldName(), "noprovice");
//                    vaildTypeEnum = PROVINCE_NAME;
//                    break;
//                case MENU:
//                    field = FieldUtils.getField(i.getClass(), MENU.getFieldName(), true);
//                    vaildTypeEnum = MENU;
//                    break;
//                case TYPE:
//                    field = ObjUtil.getField(i.getClass(), true, TYPE.getFieldName(), "datatype");
//                    vaildTypeEnum = TYPE;
//                    break;
//                case DATA_TYPE:
//                    field = ObjUtil.getField(i.getClass(), true, DATA_TYPE.getFieldName());
//                    vaildTypeEnum = DATA_TYPE;
//                    break;
//                case IMPORTEXPORT_TYPE:
//                    field = ObjUtil.getField(i.getClass(), true, IMPORTEXPORT_TYPE.getFieldName());
//                    vaildTypeEnum = IMPORTEXPORT_TYPE;
//                    break;
//                case TARGETID:
//                    field = ObjUtil.getField(i.getClass(), true, TARGETID.getFieldName(), "targetId");
//                    vaildTypeEnum = TARGETID;
//                    break;
//                case CORP_REPEAT:
//                    field = FieldUtils.getField(i.getClass(), CORP_REPEAT.getFieldName(), true);
//                    vaildTypeEnum = CORP_REPEAT;
//                    break;
//                case CORP_NAME:
//                    field = FieldUtils.getField(i.getClass(), CORP_NAME.getFieldName(), true);
//                    vaildTypeEnum = CORP_NAME;
//                    break;
//                case PRICEINFO:
//                    field = FieldUtils.getField(i.getClass(), PRICEINFO.getFieldName(), true);
//                    vaildTypeEnum = PRICEINFO;
//                    break;
//                case PRICE:
//                    field = ObjUtil.getField(i.getClass(), true, PRICE.getFieldName(), "price");
//                    vaildTypeEnum = PRICE;
//                    break;
//                case TARGETIDISSHOW:
//                    field = ObjUtil.getField(i.getClass(), true, TARGETIDISSHOW.getFieldName(), "targetId");
//                    vaildTypeEnum = TARGETIDISSHOW;
//                    break;
//                case DISTTARGETID:
//                    field = ObjUtil.getField(i.getClass(), true, DISTTARGETID.getFieldName(), "targetId");
//                    vaildTypeEnum = DISTTARGETID;
//                    break;
//                case MinPrice:
//                    field = ObjUtil.getField(i.getClass(), true, MinPrice.getFieldName(), "productId");
//                    vaildTypeEnum = MinPrice;
//                    break;
//                case MaxPrice:
//                    field = ObjUtil.getField(i.getClass(), true, MaxPrice.getFieldName(), "productId");
//                    vaildTypeEnum = MaxPrice;
//                    break;
//                case MonthPriceAvg:
//                    field = ObjUtil.getField(i.getClass(), true, MonthPriceAvg.getFieldName(), "productId");
//                    vaildTypeEnum = MonthPriceAvg;
//                    break;
//                case PriceDate:
//                    field = ObjUtil.getField(i.getClass(), true, PriceDate.getFieldName(), "priceDate");
//                    vaildTypeEnum = PriceDate;
//                    break;
//                default:
//                    throw new RuntimeException(e.getCode() + " 校验类型未定义");
//            }
//            if (setErrMsg(e.getMessage(), i, vaildTypeEnum, field)) {
//                errList.add(i);
//            }
//        });
//    }
//
//    protected void sort(List<T> tList) {
//
//    }
//
//    protected List<ImportDataGroup> group(List<T> tList) {
//        sort(tList);
//        return Arrays.asList(new ImportDataGroup(0, tList));
//    }
//
//    protected Map<Long, List<T>> group1(List<T> tList) {
//        return new HashMap<>();
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    protected class ImportDataGroup {
//        //常用productId做为分组id,一般根据插入唯一性特征生成,如果是复杂key可以使用组合
//        private Integer key;
//
//        private List<T> tList;
//
//    }
//
//    private void async1(Map<Long, List<T>> map, final List<T> errList, Long logName) throws InterruptedException {
//        ForkJoinPool forkJoinPool = new ForkJoinPool(Math.min(map.keySet().size(), THREAD_POOLSIZE));
//        forkJoinPool.submit(() -> map.keySet().parallelStream().forEach(i -> {
//            List<T> ts = map.get(i);
//            sort(ts);
//            ts.stream().forEach(t -> {
//                try {
//                    R r = saveOrUpdate(t, logName);
//                    if (!r.isSuccess()) {
//                        ObjUtil.setVal(t, r.getMsg());
//                        errList.add(t);
//                    }
//                } catch (MybatisPlusException e) {
//                    if (e.getMessage().equals("One record is expected, but the query result is multiple records")) {
//                        ObjUtil.setVal(t, "表中出现重复数据");
//                    } else {
//                        log.error(e.getMessage(), e);
//                        ObjUtil.setVal(t, "导入失败");
//                    }
//                    errList.add(t);
//                } catch (Exception e) {
//                    log.error(e.getMessage(), e);
//                    ObjUtil.setVal(t, "导入失败");
//                    errList.add(t);
//                }
//            });
//        })).join();
//        forkJoinPool.shutdown();
//        if (CollectionUtil.isNotEmpty(errList)) {
//            throw new ImportVaildException(VaildTypeEnum.CHECK, "");
//        }
//    }
//
//    protected Boolean checkIsBlock(List<T> tList) {
//        return true;
//    }
//
//    protected Boolean checkAdmin(Long userId) {
//        return true;
//    }
//
//    protected void checkProductPerssion(List<T> tList, List<T> errList) {
//        if (CollUtil.isEmpty(tList)) return;
//        Field field = ObjUtil.getField(tList.get(0).getClass(), true, "productId", "productid", "productID");
//        if (ObjectUtil.isNull(field)) return;
//        //获取用户商品权限
//        if (ContextUtil.isAdmin()) {
//            return;
//        }
//        List<Integer> userProduct = cacheUtil.getUserProduct();
//        Map<Integer, List<T>> productMap = tList.stream().collect(Collectors.groupingBy(i -> {
//            try {
//                return (Integer) field.get(i);
//            } catch (IllegalAccessException e) {
//                log.error("检查商品权限异常: " + i + "\n" + ExceptionUtils.getStackTrace(e));
//                return 0;
//            }
//        }));
//        Set<Integer> productIdSet = productMap.keySet();
//        if (importConfiguration.isProductId()) {
//            QueryWrapper<ProductEntity> queryWrapper = new QueryWrapper<>();
//            queryWrapper.in("BigdataProductID", productIdSet);
//            productIdSet = productService.list(queryWrapper).parallelStream().map(i -> i.getProductId()).collect(Collectors.toSet());
//        }
//        productIdSet.stream().forEach(i -> {
//            if (!userProduct.contains(i)) {
//                if (importConfiguration.isProductId()) {
//                    ProductEntity byId = productService.getById(i);
//                    if (ObjectUtil.isNotNull(byId)) {
//                        handleVaildErr(new ImportVaildException(VaildTypeEnum.PRODUCT_NO_PERSSION,String.valueOf(byId.getBigDataProductID()))
//                                ,productMap.get(byId.getBigDataProductID()),errList);
//                    }
//                } else {
//                    handleVaildErr(new ImportVaildException(VaildTypeEnum.PRODUCT_NO_PERSSION,String.valueOf(i)),productMap.get(i),errList);
//                }
//            }
//        });
//    }
//}

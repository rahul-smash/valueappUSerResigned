package com.signity.bonbon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.UpdateCartModel;
import com.signity.bonbon.model.Variant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rajinder on 19/10/15.
 */
public class AppDatabase {

    private DBHelper opener;
    private SQLiteDatabase db;
    Context context;
    GsonHelper gsonHelper;
    private String TAG = AppDatabase.class.getSimpleName();
    public PrefManager prefManager;

    public AppDatabase(Context context) {
        this.context = context;
        this.opener = new DBHelper(context);
        db = opener.getWritableDatabase();
        gsonHelper = new GsonHelper();
        prefManager=new PrefManager(context);
    }

    // operation related to category table
    public void addCategoryList(List<Category> data) {
        if (data != null && data.size() != 0) {

            deleteOperationForVersionUpdate(data);
            deleteCategoryAll();

            for (Category category : data) {
                try {
                    ContentValues values = new ContentValues();
                    values.put("id", category.getId());
                    values.put("title", category.getTitle());
                    values.put("image", category.getImage());
                    values.put("image_small", category.getImageSmall());
                    values.put("image_medium", category.getImageMedium());
                    values.put("image_medium", category.getImageMedium());
                    values.put("image_medium", category.getImageMedium());
                    values.put("sub_category", gsonHelper.getSubCategory(category.getSubCategoryList()));
                    values.put("is_enable", category.getIsEnable());
                    values.put("is_deleted", category.isDeleted() ? "1" : "0");
                    try {
                        values.put("sort_order", Integer.parseInt((category.getSortOrder() != null && !
                                (category.getSortOrder().isEmpty())) ? category.getSortOrder() : "0"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    Category dbCategory = getCategoryById(category.getId());
                    if (dbCategory != null) {
                        if (!((dbCategory.getVersion()).equals(category.getVersion()))) {
                            values.put("version", category.getVersion());
                            updateVersionSubCategoryIfExist(category.getSubCategoryList());
                            long l = db.update("category", values, "id=?", new String[]{category.getId()});
                            Log.i(TAG, "--------Category--------" + category.getTitle() +
                                    "--------Updated--------with version ----" + category.getVersion() + "------from-----" + dbCategory.getVersion());
                        }
                    } else {
                        values.put("version", category.getVersion());
                        long l = db.insert("category", null, values);
                        Log.i(TAG, "-------Category-------" + category.getTitle() + "-------Added-------Version-----" + category.getVersion());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Category> getCategoryList() {
        List<Category> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US, "SELECT * FROM category where is_enable=%s AND is_deleted=%s ORDER BY sort_order ASC", "1", "0");
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Category category = new Category();
                category.setId(cursor.getString(0));
                category.setTitle(cursor.getString(1));
                category.setImage(cursor.getString(2));
                category.setSubCategoryList(gsonHelper.getSubCategory(cursor.getString(3)));
                category.setVersion(cursor.getString(4));
                category.setImageSmall(cursor.getString(5));
                category.setImageMedium(cursor.getString(6));
                category.setIsEnable(cursor.getString(7));
                category.setIsDeleted(cursor.getString(8).equals("1") ? true : false);
                category.setSortOrder(String.valueOf(cursor.getString(9)));
                cursor.moveToNext();
                list.add(category);
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Category getCategoryById(String id) {
        Category category = null;

        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM category where id=%s", id);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                category = new Category();
                category.setId(cursor.getString(0));
                category.setTitle(cursor.getString(1));
                category.setImage(cursor.getString(2));
                category.setSubCategoryList(gsonHelper.getSubCategory(cursor.getString(3)));
                category.setVersion(cursor.getString(4));
                category.setImageSmall(cursor.getString(5));
                category.setImageMedium(cursor.getString(6));
                category.setIsEnable(cursor.getString(7));
                category.setIsDeleted(cursor.getString(8).equals("1") ? true : false);
                category.setSortOrder(String.valueOf(cursor.getString(9)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return category;
    }


    /*End Category operations here*/


    /*Delete Operation for any version change*/

    private void deleteOperationForVersionUpdate(List<Category> data) {

        if (data != null && data.size() != 0) {
            for (Category cat : data) {
                Category dbCategory = getCategoryById(cat.getId());
                if (dbCategory != null)
                    if (!((dbCategory.getVersion()).equals(cat.getVersion()))) {
                        deleteSubCategoryForVersionUpdate(cat.getSubCategoryList());
                        deleteSubCategoryForCategroryId(cat.getId());
                    }


            }
        }

    }

    private void deleteSubCategoryForVersionUpdate(List<SubCategory> subCategoryList) {
        if (subCategoryList != null && subCategoryList.size() != 0) {
            for (SubCategory subCategory : subCategoryList) {
                SubCategory dbSubCategory = getSubCategoryById(subCategory.getId());
                if (dbSubCategory != null)
                    if (!((dbSubCategory.getVersion()).equals(subCategory.getVersion()))) {
                        deleteProductForSubCategoryChange(subCategory.getId());
                    }
            }
        }

    }

    private void deleteProductForSubCategoryChange(String sub_cat_id) {
        try {
            db.delete("product", "category_ids = ?", new String[]{sub_cat_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSubCategoryForCategroryId(String catId) {
        try {
            db.delete("sub_category", "parent_id = ?", new String[]{catId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCategoryAll() {
        try {
            db.delete("category", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*Delete operation ends here*/


    // Operation related to Sub category and Product table
//    id TEXT UNIQUE PRIMARY KEY NOT NULL,
//    title TEXT,
//    image TEXT,
//    parent_id TEXT,
//    product_id TEXT,
//    version TEXT,
//    old_version TEXT,
//    image_small TEXT,
//    image_medium TEXT
    public boolean addSubCategoryToDb(SubCategory subCategory) {
        boolean status = false;
        try {
            ContentValues values = new ContentValues();
            values.put("id", subCategory.getId());
            values.put("title", subCategory.getTitle());
            values.put("image", subCategory.getImage());
            values.put("parent_id", subCategory.getParentId());
            values.put("version", subCategory.getVersion());
            values.put("image_small", subCategory.getImageSmall());
            values.put("image_medium", subCategory.getImageMedium());
            values.put("sort_order", Integer.parseInt((subCategory.getSortOrder() != null &&
                    !(subCategory.getSortOrder().equalsIgnoreCase(""))) ? subCategory.getSortOrder() : "0"));
            SubCategory subCategoryDb = getSubCategoryById(subCategory.getId());
            if (subCategoryDb != null) {
                if (!(subCategory.getVersion()).equals(subCategoryDb.getOldVersion())) {
                    values.put("old_version", subCategory.getVersion());
                    List<Product> products = subCategory.getProducts();
                    addListProduct(products, subCategory.getId());
                    long l = db.update("sub_category", values, "id=?", new String[]{subCategory.getId()});
                    Log.i(TAG, "----------Sub Category----------" + subCategory.getTitle() + "-----with version------" +
                            subCategory.getVersion() + "----------Updated------with version-----" + subCategory.getVersion() +
                            "----- from" + subCategoryDb.getOldVersion());
                }

            } else {
                values.put("old_version", subCategory.getVersion());
                List<Product> products = subCategory.getProducts();
                addListProduct(products, subCategory.getId());
                long l = db.insert("sub_category", null, values);
                Log.i(TAG, "----------Sub Category----------" + subCategory.getTitle() + "-----with verision----" + subCategory.getVersion() + "-------Added-------");
            }


        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }


    public void updateVersionSubCategoryIfExist(List<SubCategory> subCategoryList) {

        for (int i = 0; i < subCategoryList.size(); i++) {
            SubCategory subCategory = subCategoryList.get(i);
            SubCategory subCategoryDb = getSubCategoryById(subCategoryList.get(i).getId());

            if (subCategoryDb != null) {
                try {
                    ContentValues values = new ContentValues();
                    values.put("version", subCategory.getVersion());
                    long l = db.update("sub_category", values, "id=?", new String[]{subCategory.getId()});
                    Log.i(TAG, "----------Sub Category version----------" + subCategory.getTitle() + "." + subCategory.getVersion() + "----------updated----------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public SubCategory getSubCategoryById(String id) {
        SubCategory subCategory = null;

        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM sub_category where id=%s ORDER BY sort_order ASC", id);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                subCategory = new SubCategory();
                subCategory.setId(cursor.getString(0));
                subCategory.setTitle(cursor.getString(1));
                subCategory.setImage(cursor.getString(2));
                subCategory.setParentId(cursor.getString(3));
                subCategory.setProductId(cursor.getString(4));
                subCategory.setVersion(cursor.getString(5));
                subCategory.setOldVersion(cursor.getString(6));
                subCategory.setImageSmall(cursor.getString(7));
                subCategory.setImageMedium(cursor.getString(8));
                subCategory.setSortOrder(String.valueOf(cursor.getString(9)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return subCategory;
    }


    // SUB_CATEGORY OPERATION ENDS HERE


    //Product operation starts

//    CREATE TABLE IF NOT EXISTS product (
//            p_id INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,
//            id TEXT,
//            store_id TEXT,
//            category_ids TEXT,
//            title TEXT,
//            brand TEXT,
//            nutrient TEXT,
//            description TEXT,
//            image TEXT,
//            show_price TEXT,
//            favorites INTEGER,
//            image_100_80 TEXT,
//            image_300_200 TEXT,
//            variants TEXT,
//            selectedVariant TEXT
//    );

    public void addListProduct(List<Product> listProduct, String cateId) {

        if (listProduct != null && listProduct.size() != 0) {
            for (Product product : listProduct) {
                try {
                    ContentValues values = new ContentValues();
                    values.put("id", product.getId());
                    values.put("store_id", product.getStoreId());
                    values.put("category_ids", cateId);
                    values.put("title", product.getTitle());
                    values.put("brand", product.getBrand());
                    values.put("nutrient", product.getNutrient());
                    values.put("description", product.getDescription());
                    values.put("image", product.getImage());
                    values.put("show_price", product.getShowPrice());
                    values.put("favorites", product.isFavorites() ? "1" : "0");
                    values.put("is_enable", product.getIsEnable());
                    values.put("is_deleted", product.isDeleted() ? "1" : "0");
                    values.put("image_100_80", product.getImageSmall());
                    values.put("image_300_200", product.getImageMedium());
                    values.put("sort_order", Integer.parseInt(product.getSortOrder()));
                    values.put("isTaxEnable", product.getIsTaxEnable());
                    values.put("variants", gsonHelper.getProductVarientsArray(product.getVariants()));
                    Product pro = getProduct(product.getId());
                    if (pro != null) {
                        SelectedVariant selectedVariantUpdate = changeSelectedVarientForProductUpdate(product.getVariants(), pro.getSelectedVariant());
                        product.setSelectedVariant(selectedVariantUpdate);
                        values.put("selectedVariant", gsonHelper.getSelectedVarient(product.getSelectedVariant()));
                        long l = db.update("product", values, "id" + "=?", new String[]{String.valueOf(product.getId())});
                        Log.i(TAG, "---------Product---------" + product.getTitle() + "---------updated---------");
                        updateToCartForProductChanges(product);
                    } else {
                        values.put("selectedVariant", gsonHelper.getSelectedVarient(product.getSelectedVariant()));
                        long l = db.insert("product", null, values);
                        Log.i(TAG, "-----------Product--------" + product.getTitle() + "--------Added--------");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private SelectedVariant changeSelectedVarientForProductUpdate(List<Variant> variants, SelectedVariant selectedVariant) {

        for (Variant variant : variants) {

            if ((variant.getId()).equals(selectedVariant.getVariantId())) {
                selectedVariant.setWeight(variant.getWeight());
                selectedVariant.setUnitType(variant.getUnitType());
                selectedVariant.setMrpPrice(variant.getMrpPrice());
                selectedVariant.setPrice(variant.getPrice());
                selectedVariant.setDiscount(variant.getDiscount());
                break;
            }

        }


        return selectedVariant;
    }


    public void updateProduct(Product product) {

        try {
            if (product != null) {
                ContentValues values = new ContentValues();
                values.put("id", product.getId());
                values.put("store_id", product.getStoreId());
                values.put("category_ids", product.getCategoryIds());
                values.put("title", product.getTitle());
                values.put("brand", product.getBrand());
                values.put("nutrient", product.getNutrient());
                values.put("description", product.getDescription());
                values.put("image", product.getImage());
                values.put("show_price", product.getShowPrice());
                values.put("favorites", product.isFavorites() ? "1" : "0");
                values.put("image_100_80", product.getImageSmall());
                values.put("image_300_200", product.getImageMedium());
                values.put("sort_order", Integer.parseInt(product.getSortOrder()));
                values.put("isTaxEnable", product.getIsTaxEnable());
                values.put("variants", gsonHelper.getProductVarientsArray(product.getVariants()));
                values.put("selectedVariant", gsonHelper.getSelectedVarient(product.getSelectedVariant()));
                long l = db.update("product", values, "id" + "=?", new String[]{String.valueOf(product.getId())});
                if (l == 0) {
                    long l2 = db.insert("product", null, values);
                    Log.i("TAG", "---------Product---------" + product.getTitle() + "------Added------");
                } else {
                    Log.i("TAG", "---------Product---------" + product.getTitle() + "------Updated------");
                }


            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }


//    store_id TEXT,
//    category_ids TEXT,
//    title TEXT,
//    brand TEXT,
//    nutrient TEXT,
//    description TEXT,
//    image TEXT,
//    show_price TEXT,
//    favorites INTEGER,
//    image_100_80 TEXT,
//    image_300_200 TEXT,
//    variants TEXT,
//    selectedVariant TEXT

    public Product getProduct(String productId) {
        Product product = null;
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM product where id=%s", productId);
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();

            if (size == 1) {
                cursor.moveToFirst();
                product = new Product();
                product.setId(cursor.getString(0));
                product.setStoreId(cursor.getString(1));
                product.setCategoryIds(cursor.getString(2));
                product.setTitle(cursor.getString(3));
                product.setBrand(cursor.getString(4));
                product.setNutrient(cursor.getString(5));
                product.setDescription(cursor.getString(6));
                product.setImage(cursor.getString(7));
                product.setShowPrice(cursor.getString(8));
                product.setFavorites(cursor.getString(9).equals("1") ? true : false);
                product.setImageSmall(cursor.getString(10));
                product.setImageMedium(cursor.getString(11));
                product.setVariants(gsonHelper.getProductVarientsArray(cursor.getString(12)));
                product.setSelectedVariant(gsonHelper.getSelectedVarient(cursor.getString(13)));
                product.setIsEnable(cursor.getString(14));
                product.setIsDeleted(cursor.getString(15).equals("1") ? true : false);
                product.setSortOrder(String.valueOf(cursor.getString(16)));
                product.setIsTaxEnable(cursor.getString(17));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return product;

    }

    public String getProductName(String productId) {
        Product product = null;
        Cursor cursor = null;
        String productName = "";
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM product where id=%s", productId);
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();

            if (size == 1) {
                cursor.moveToFirst();
                productName = cursor.getString(3);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
            productName = "";
        }
        if (productName.contains("&")) {
            productName = productName.replace("&", "");
        }

        return productName;

    }


    public List<Product> getProductList(String subCategoryId) {

        List<Product> listProduct = new ArrayList<>();

        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM product where category_ids=%s AND is_enable=%s AND is_deleted=%s ORDER BY sort_order ASC", subCategoryId, "1", "0");
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setStoreId(cursor.getString(1));
                product.setCategoryIds(cursor.getString(2));
                product.setTitle(cursor.getString(3));
                product.setBrand(cursor.getString(4));
                product.setNutrient(cursor.getString(5));
                product.setDescription(cursor.getString(6));
                product.setImage(cursor.getString(7));
                product.setShowPrice(cursor.getString(8));
                product.setFavorites(cursor.getString(9).equals("1") ? true : false);
                product.setImageSmall(cursor.getString(10));
                product.setImageMedium(cursor.getString(11));
                product.setVariants(gsonHelper.getProductVarientsArray(cursor.getString(12)));
                product.setSelectedVariant(gsonHelper.getSelectedVarient(cursor.getString(13)));
                product.setIsEnable(cursor.getString(14));
                product.setIsDeleted(cursor.getString(15).equals("1") ? true : false);
                product.setSortOrder(String.valueOf(cursor.getString(16)));
                product.setIsTaxEnable(cursor.getString(17));
                cursor.moveToNext();
                listProduct.add(product);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) cursor.close();
        }
        return listProduct;

    }

    public List<Product> getUserFavProductList() {

        List<Product> listProduct = new ArrayList<>();

        Cursor cursor = null;

        String sql = String.format(Locale.US,
                "SELECT * FROM product where favorites =%s", "1");
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            try {
                Product product = new Product();
                product.setId(cursor.getString(0));
                product.setStoreId(cursor.getString(1));
                product.setCategoryIds(cursor.getString(2));
                product.setTitle(cursor.getString(3));
                product.setBrand(cursor.getString(4));
                product.setNutrient(cursor.getString(5));
                product.setDescription(cursor.getString(6));
                product.setImage(cursor.getString(7));
                product.setShowPrice(cursor.getString(8));
                product.setFavorites(cursor.getString(9).equals("1") ? true : false);
                product.setImageSmall(cursor.getString(10));
                product.setImageMedium(cursor.getString(11));
                product.setVariants(gsonHelper.getProductVarientsArray(cursor.getString(12)));
                product.setSelectedVariant(gsonHelper.getSelectedVarient(cursor.getString(13)));
                product.setIsEnable(cursor.getString(14));
                product.setIsDeleted(cursor.getString(15).equals("1") ? true : false);
                product.setSortOrder(String.valueOf(cursor.getString(16)));
                product.setIsTaxEnable(cursor.getString(17));
                cursor.moveToNext();
                listProduct.add(product);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return listProduct;
    }


    public double countTax(String tax,String price){
        double tax_amount = ((Double.parseDouble(price) * Double.parseDouble(tax) / 100));
        return tax_amount;
    }


    public void updateToCart(Product product) {
        boolean isAlreadyExit = false;
        isAlreadyExit = isProductInCart(product);
        try {
            if (product != null) {
                SelectedVariant selectedVarient = product.getSelectedVariant();
                if (selectedVarient.getQuantity().equals("0")) {
                    long deleteRow = db.delete("cart_table", "variant_id" + "=?", new String[]{String.valueOf(product.getSelectedVariant().getVariantId())});
                    if (deleteRow != 0) {
                        Log.i(TAG, "--------Cart----" + product.getTitle() + "--------Removed--------");
                    }
                } else {
                    ContentValues values = new ContentValues();
                    values.put("variant_id", selectedVarient.getVariantId());
                    values.put("product_id", product.getId());
                    values.put("weight", selectedVarient.getWeight());
                    values.put("mrp_price", selectedVarient.getMrpPrice());
                    values.put("price", selectedVarient.getPrice());
                    values.put("discount", selectedVarient.getDiscount());
                    values.put("unit_type", selectedVarient.getUnitType());
                    values.put("quantity", selectedVarient.getQuantity());

                    String isTaxEnable=prefManager.getSharedValue(AppConstant.istaxenable);

                    if(isTaxEnable.equalsIgnoreCase("0")){
                        values.put("tax", "0");
                    }else if(isTaxEnable.equalsIgnoreCase("1")){

                        if(product.getIsTaxEnable().equalsIgnoreCase("0")){
                            values.put("tax", "0");
                        }else {
                            String tax=prefManager.getSharedValue(AppConstant.tax_rate);
                            values.put("tax", String.valueOf(countTax(tax,selectedVarient.getPrice())));
                        }
                    }

                    if (isAlreadyExit) {
                        long l = db.update("cart_table", values, "variant_id=?", new String[]{
                                selectedVarient.getVariantId()
                        });
                        Log.i(TAG, "--------Cart-----+" + product.getTitle() + "-----Updated----");
                    } else {
                        long l = db.insert("cart_table", null, values);
                        Log.i(TAG, "--------Cart-----+" + product.getTitle() + "-----Added----");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error", e.getMessage());
        }
    }

    public void addToCart(UpdateCartModel updateCartModel) {
        boolean isAlreadyExit = false;
        isAlreadyExit = isProductInCart(updateCartModel.getVariantId());
        try {
            ContentValues values = new ContentValues();
            values.put("variant_id", updateCartModel.getVariantId());
            values.put("product_id", updateCartModel.getProductId());
            values.put("weight", updateCartModel.getWeight());
            values.put("mrp_price", updateCartModel.getMrpPrice());
            values.put("price", updateCartModel.getPrice());
            values.put("discount", updateCartModel.getDiscount());
            values.put("unit_type", updateCartModel.getUnitType());
            values.put("quantity", updateCartModel.getQuantity());

            String isTaxEnable=prefManager.getSharedValue(AppConstant.istaxenable);

            if(isTaxEnable.equalsIgnoreCase("0")){
                values.put("tax", "0");
            }else if(isTaxEnable.equalsIgnoreCase("1")){

                if(updateCartModel.getIsTaxEnable().equalsIgnoreCase("0")){
                    values.put("tax", "0");
                }else {
                    String tax=prefManager.getSharedValue(AppConstant.tax_rate);
                    values.put("tax", String.valueOf(countTax(tax,updateCartModel.getPrice())));
                }
            }

            if (isAlreadyExit) {
                long l = db.update("cart_table", values, "variant_id=?", new String[]{
                        updateCartModel.getVariantId()
                });
                Log.i(TAG, "--------Cart-----+" + updateCartModel.getVariantId() + "-----Updated----");
            } else {
                long l = db.insert("cart_table", null, values);
                Log.i(TAG, "--------Cart-----+" + updateCartModel.getVariantId() + "-----Added----");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Log.i("Error", exception.getMessage());
        }


    }

    public void updateToCartForProductChanges(Product product) {
        boolean isAlreadyExit = false;
        isAlreadyExit = isProductInCart(product);

        try {
            if (isAlreadyExit) {
                SelectedVariant selectedVarient = product.getSelectedVariant();
                ContentValues values = new ContentValues();
                values.put("variant_id", selectedVarient.getVariantId());
                values.put("product_id", product.getId());
                values.put("weight", selectedVarient.getWeight());
                values.put("mrp_price", selectedVarient.getMrpPrice());
                values.put("price", selectedVarient.getPrice());
                values.put("discount", selectedVarient.getDiscount());
                values.put("unit_type", selectedVarient.getUnitType());
                values.put("quantity", selectedVarient.getQuantity());

                String isTaxEnable=prefManager.getSharedValue(AppConstant.istaxenable);

                if(isTaxEnable.equalsIgnoreCase("0")){
                    values.put("tax", "0");
                }else if(isTaxEnable.equalsIgnoreCase("1")){

                    if(product.getIsTaxEnable().equalsIgnoreCase("0")){
                        values.put("tax", "0");
                    }else {
                        String tax=prefManager.getSharedValue(AppConstant.tax_rate);
                        values.put("tax", String.valueOf(countTax(tax,selectedVarient.getPrice())));
                    }
                }

                long l = db.update("cart_table", values, "variant_id=?", new String[]{
                        selectedVarient.getVariantId()
                });
                Log.i(TAG, "--------Product Changes from Version -----+" + product.getTitle() + "-----Updated To Cart----");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isProductInCart(Product product) {
        boolean status = false;
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM cart_table where variant_id=%s", product.getSelectedVariant().getVariantId());
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();
            if (size > 0) {
                status = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return status;
    }

    public boolean isProductInCart(String varientID) {
        boolean status = false;
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM cart_table where variant_id=%s", varientID);
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();
            if (size > 0) {
                status = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
        }
        return status;
    }

    public String getCartQuantity(String id) {
        Cursor cursor = null;
        String quantity = "0";
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM cart_table where variant_id=%s", id);
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();
            if (size == 1) {
                cursor.moveToFirst();
                quantity = cursor.getString(7);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return quantity;
    }


    public int getCartSize() {
        List<Category> list = new ArrayList<>();

        int size = 0;
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM cart_table where 1=1";
            cursor = db.rawQuery(sql, null);
            size = cursor.getCount();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return size;
    }

    public List<UpdateCartModel> getCartList() {
        List<UpdateCartModel> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM cart_table where 1=1";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                UpdateCartModel updateCartModel = new UpdateCartModel();
                updateCartModel.setVariantId(cursor.getString(0));
                updateCartModel.setProductId(cursor.getString(1));
                updateCartModel.setWeight(cursor.getString(2));
                updateCartModel.setMrpPrice(cursor.getString(3));
                updateCartModel.setPrice(cursor.getString(4));
                updateCartModel.setDiscount(cursor.getString(5));
                updateCartModel.setUnitType(cursor.getString(6));
                updateCartModel.setQuantity(cursor.getString(7));
                updateCartModel.setProductName(getProductName(cursor.getString(1)));
                updateCartModel.setTax(cursor.getString(8));
                cursor.moveToNext();
                list.add(updateCartModel);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return list;
    }

    public void deleteCartElement() {

        List<UpdateCartModel> updateCartModelList = getCartList();
        for (UpdateCartModel updateCartModel : updateCartModelList) {
            Product product = getProduct(updateCartModel.getProductId());
            SelectedVariant selectedVariant = product.getSelectedVariant();
            selectedVariant.setQuantity("0");
            selectedVariant.setVariantId(updateCartModel.getVariantId());
            updateToCart(product);
            selectedVariant.setVariantId("0");
            selectedVariant.setSku("0");
            selectedVariant.setWeight("0");
            selectedVariant.setMrpPrice("0");
            selectedVariant.setPrice("0");
            selectedVariant.setDiscount("0");
            selectedVariant.setUnitType("0");
            updateProduct(product);
        }
    }


    public List<Product> getCartListProduct() {
        List<Product> list = new ArrayList<>();
        List<UpdateCartModel> listUpdateCart = getCartList();

        for (UpdateCartModel updateCartModel : listUpdateCart) {
            Product product = getProduct(updateCartModel.getProductId());
            SelectedVariant selectedVarient = getSelctedVarient(product, updateCartModel.getVariantId());
            selectedVarient.setQuantity(getCartQuantity(selectedVarient.getVariantId()));
            product.setSelectedVariant(selectedVarient);
            if (product != null) {
                list.add(product);
            }
        }
        return list;
    }

    public String getCartListStringJson() {
        String jsonString = "";
        List<UpdateCartModel> updateCartModelList = getCartList();
        Gson gson = new Gson();
        Type type = new TypeToken<List<UpdateCartModel>>() {
        }.getType();
        jsonString = gson.toJson(updateCartModelList, type);
        Log.i("TAG", jsonString);
        return jsonString;
    }


    private SelectedVariant getSelctedVarient(Product product, String variantId) {
        List<Variant> listVarient = product.getVariants();
        SelectedVariant selectedVariant = product.getSelectedVariant();
        for (int i = 0; i < listVarient.size(); i++) {
            Variant variant = listVarient.get(i);
            if (variant.getId().equals(variantId)) {
                selectedVariant.setVarientPosition(i);
                selectedVariant.setVariantId(variant.getId());
                selectedVariant.setSku(variant.getSku());
                selectedVariant.setWeight(variant.getWeight());
                selectedVariant.setMrpPrice(variant.getMrpPrice());
                selectedVariant.setPrice(variant.getPrice());
                selectedVariant.setDiscount(variant.getDiscount());
                selectedVariant.setUnitType(variant.getUnitType());
                break;
            }

        }

        return selectedVariant;
    }

    public String getCartTotalPrice() {
        double totalPrice = 0.0;
        List<UpdateCartModel> listCartModel = getCartList();

        for (UpdateCartModel updateCartModel : listCartModel) {
            int quan = Integer.parseInt(updateCartModel.getQuantity());
            double price = Double.parseDouble(updateCartModel.getPrice());
            double productPrice = quan * price;
            totalPrice = totalPrice + productPrice;
        }
        return String.valueOf(totalPrice);
    }

    public String getTotalTax() {
        double totalTax = 0.0;
        List<UpdateCartModel> listCartModel = getCartList();

        for (UpdateCartModel updateCartModel : listCartModel) {
            int quan = Integer.parseInt(updateCartModel.getQuantity());
            double tax = Double.parseDouble(updateCartModel.getTax());
            double productPrice = quan * tax;
            totalTax = totalTax + productPrice;
        }
        return String.valueOf(totalTax);
    }


    ///--------------------------------------------Store Table---------------------------
    //
//    CREATE TABLE IF NOT EXISTS store (
//            id TEXT UNIQUE PRIMARY KEY NOT NULL, store_name TEXT, location TEXT, city TEXT,state TEXT,
//            country TEXT, zipcode TEXT, contact_person TEXT,  contact_number TEXT, banner TEXT,
//            about_us TEXT,    version TEXT
//    );
    public void addStore(Store store) {

        String productId = "";
        try {
            if (store != null) {
                ContentValues values = new ContentValues();
                values.put("id", store.getId());
                values.put("store_name", store.getStoreName());
                values.put("location", store.getLocation());
                values.put("city", store.getCity());
                values.put("state", store.getState());
                values.put("country", store.getCountry());
                values.put("zipcode", store.getZipcode());
                values.put("contact_person", store.getContactPerson());
                values.put("contact_number", store.getContactNumber());
                values.put("banner", store.getBanner());
                values.put("about_us", store.getAboutUs());
                values.put("version", store.getVersion());
                values.put("lat", store.getLat());
                values.put("lng", store.getLng());
                values.put("otp_skip", store.getOtpSkip());
                values.put("store_status", store.getStoreStatus());
                values.put("android_app_share", store.getAndroidAppShareLink());
                values.put("type", store.getType());
                values.put("theme", store.getTheme());
                if (isStoreInCart(store.getId())) {
                    long l = db.update("store", values, "id=?", new String[]{store.getId()});
                    Log.i(TAG, "-----------store-----------" + store.getId() + "---updated---");
                } else {
                    long l = db.insert("store", null, values);
                    Log.i(TAG, "-----------store-----------" + store.getId() + "---Added---");
                }
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public boolean isStoreInCart(String storeId) {
        boolean status = false;
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM store where id=%s", storeId);
            cursor = db.rawQuery(sql, null);
            int size = cursor.getCount();
            if (size > 0) {
                status = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return status;
    }


    public Store getStore(String storeId) {
        Store store = null;
        Cursor cursor = null;
        try {
            String sql = String.format(Locale.US,
                    "SELECT * FROM store where id=%s", storeId);
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                store = new Store();
                store.setId(cursor.getString(0));
                store.setStoreName(cursor.getString(1));
                store.setLocation(cursor.getString(2));
                store.setCity(cursor.getString(3));
                store.setState(cursor.getString(4));
                store.setCountry(cursor.getString(5));
                store.setZipcode(cursor.getString(6));
                store.setContactPerson(cursor.getString(7));
                store.setContactNumber(cursor.getString(8));
                store.setBanner(cursor.getString(9));
                store.setAboutUs(cursor.getString(10));
                store.setVersion(cursor.getString(11));
                store.setLat(cursor.getString(12));
                store.setLng(cursor.getString(13));
                store.setOtpSkip(cursor.getString(14));
                store.setStoreStatus(cursor.getString(15));
                store.setAndroidAppShareLink(cursor.getString(16));
                store.setType(cursor.getString(17));
                store.setTheme(cursor.getString(18));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }
        return store;

    }


//-------- SQL LITE DATABASE

    class DBHelper extends SQLiteOpenHelper {

        public String stringFromAssets(String fileName) {
            StringBuilder ReturnString = new StringBuilder();
            InputStream fIn = null;
            InputStreamReader isr = null;
            BufferedReader input = null;
            try {
                fIn = context.getResources().getAssets()
                        .open(fileName, Context.MODE_PRIVATE);
                isr = new InputStreamReader(fIn);
                input = new BufferedReader(isr);
                String line = "";
                while ((line = input.readLine()) != null) {
                    ReturnString.append(line);
                }
            } catch (Exception e) {
                e.getMessage();
            } finally {
                try {
                    if (isr != null)
                        isr.close();
                    if (fIn != null)
                        fIn.close();
                    if (input != null)
                        input.close();
                } catch (Exception e2) {
                    e2.getMessage();
                }
            }
            return ReturnString.toString();
        }

        public DBHelper(Context context) {
            super(context, BuildConfig.DATABASE_NAME, null, BuildConfig.DB_VERSION);
        }

        // onCreate is called once if database not exists.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(this.stringFromAssets("sql/store.ddl"));
            db.execSQL(this.stringFromAssets("sql/category.ddl"));
            db.execSQL(this.stringFromAssets("sql/sub_category.ddl"));
            db.execSQL(this.stringFromAssets("sql/product.ddl"));
            db.execSQL(this.stringFromAssets("sql/cart_table.ddl"));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("Grocers", "------Database Version : Old Version:" + oldVersion
                    + "  New Version:" + newVersion + "------------");
            // some of the store element are added so better to drop the store table and  recreate this
            if (newVersion != oldVersion) {
                String script = this.stringFromAssets("sql/alter_store_version_two.ddl");
                String[] queries = script.split(";");
                for (String query : queries) {
                    try {
                        db.execSQL(query);
                    } catch (SQLException e) {
                        Log.e("Sqlite Error", e.getMessage());
                    } catch (Exception e) {
                        Log.e("Sqlite Error", e.getMessage());
                    }
                }
                String scriptForProduct = this.stringFromAssets("sql/alter_tables_version_three.ddl");
                String[] queriesForProduct = scriptForProduct.split(";");
                for (String query : queriesForProduct) {
                    try {
                        db.execSQL(query);
                    } catch (SQLException e) {
                        Log.e("Sqlite Error", e.getMessage());
                    } catch (Exception e) {
                        Log.e("Sqlite Error", e.getMessage());
                    }
                }
            }
        }

    }

}
